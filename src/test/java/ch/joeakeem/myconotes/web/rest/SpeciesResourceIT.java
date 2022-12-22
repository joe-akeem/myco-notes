package ch.joeakeem.myconotes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.joeakeem.myconotes.IntegrationTest;
import ch.joeakeem.myconotes.domain.Genus;
import ch.joeakeem.myconotes.domain.Species;
import ch.joeakeem.myconotes.repository.SpeciesRepository;
import ch.joeakeem.myconotes.repository.search.SpeciesSearchRepository;
import ch.joeakeem.myconotes.service.SpeciesService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link SpeciesResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class SpeciesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMON_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMMON_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/species";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/species";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SpeciesRepository speciesRepository;

    @Mock
    private SpeciesRepository speciesRepositoryMock;

    @Mock
    private SpeciesService speciesServiceMock;

    @Autowired
    private SpeciesSearchRepository speciesSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSpeciesMockMvc;

    private Species species;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Species createEntity(EntityManager em) {
        Species species = new Species().name(DEFAULT_NAME).commonName(DEFAULT_COMMON_NAME).description(DEFAULT_DESCRIPTION);
        // Add required entity
        Genus genus;
        if (TestUtil.findAll(em, Genus.class).isEmpty()) {
            genus = GenusResourceIT.createEntity(em);
            em.persist(genus);
            em.flush();
        } else {
            genus = TestUtil.findAll(em, Genus.class).get(0);
        }
        species.setGenus(genus);
        return species;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Species createUpdatedEntity(EntityManager em) {
        Species species = new Species().name(UPDATED_NAME).commonName(UPDATED_COMMON_NAME).description(UPDATED_DESCRIPTION);
        // Add required entity
        Genus genus;
        if (TestUtil.findAll(em, Genus.class).isEmpty()) {
            genus = GenusResourceIT.createUpdatedEntity(em);
            em.persist(genus);
            em.flush();
        } else {
            genus = TestUtil.findAll(em, Genus.class).get(0);
        }
        species.setGenus(genus);
        return species;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        speciesSearchRepository.deleteAll();
        assertThat(speciesSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        species = createEntity(em);
    }

    @Test
    @Transactional
    void createSpecies() throws Exception {
        int databaseSizeBeforeCreate = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        // Create the Species
        restSpeciesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(species)))
            .andExpect(status().isCreated());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Species testSpecies = speciesList.get(speciesList.size() - 1);
        assertThat(testSpecies.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpecies.getCommonName()).isEqualTo(DEFAULT_COMMON_NAME);
        assertThat(testSpecies.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createSpeciesWithExistingId() throws Exception {
        // Create the Species with an existing ID
        species.setId(1L);

        int databaseSizeBeforeCreate = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restSpeciesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(species)))
            .andExpect(status().isBadRequest());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        // set the field null
        species.setName(null);

        // Create the Species, which fails.

        restSpeciesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(species)))
            .andExpect(status().isBadRequest());

        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllSpecies() throws Exception {
        // Initialize the database
        speciesRepository.saveAndFlush(species);

        // Get all the speciesList
        restSpeciesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(species.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].commonName").value(hasItem(DEFAULT_COMMON_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpeciesWithEagerRelationshipsIsEnabled() throws Exception {
        when(speciesServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSpeciesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(speciesServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllSpeciesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(speciesServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restSpeciesMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(speciesRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getSpecies() throws Exception {
        // Initialize the database
        speciesRepository.saveAndFlush(species);

        // Get the species
        restSpeciesMockMvc
            .perform(get(ENTITY_API_URL_ID, species.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(species.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.commonName").value(DEFAULT_COMMON_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingSpecies() throws Exception {
        // Get the species
        restSpeciesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingSpecies() throws Exception {
        // Initialize the database
        speciesRepository.saveAndFlush(species);

        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();
        speciesSearchRepository.save(species);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());

        // Update the species
        Species updatedSpecies = speciesRepository.findById(species.getId()).get();
        // Disconnect from session so that the updates on updatedSpecies are not directly saved in db
        em.detach(updatedSpecies);
        updatedSpecies.name(UPDATED_NAME).commonName(UPDATED_COMMON_NAME).description(UPDATED_DESCRIPTION);

        restSpeciesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSpecies.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSpecies))
            )
            .andExpect(status().isOk());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        Species testSpecies = speciesList.get(speciesList.size() - 1);
        assertThat(testSpecies.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpecies.getCommonName()).isEqualTo(UPDATED_COMMON_NAME);
        assertThat(testSpecies.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Species> speciesSearchList = IterableUtils.toList(speciesSearchRepository.findAll());
                Species testSpeciesSearch = speciesSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testSpeciesSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testSpeciesSearch.getCommonName()).isEqualTo(UPDATED_COMMON_NAME);
                assertThat(testSpeciesSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingSpecies() throws Exception {
        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        species.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpeciesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, species.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(species))
            )
            .andExpect(status().isBadRequest());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchSpecies() throws Exception {
        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        species.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpeciesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(species))
            )
            .andExpect(status().isBadRequest());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSpecies() throws Exception {
        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        species.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpeciesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(species)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateSpeciesWithPatch() throws Exception {
        // Initialize the database
        speciesRepository.saveAndFlush(species);

        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();

        // Update the species using partial update
        Species partialUpdatedSpecies = new Species();
        partialUpdatedSpecies.setId(species.getId());

        partialUpdatedSpecies.name(UPDATED_NAME);

        restSpeciesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpecies.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpecies))
            )
            .andExpect(status().isOk());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        Species testSpecies = speciesList.get(speciesList.size() - 1);
        assertThat(testSpecies.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpecies.getCommonName()).isEqualTo(DEFAULT_COMMON_NAME);
        assertThat(testSpecies.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateSpeciesWithPatch() throws Exception {
        // Initialize the database
        speciesRepository.saveAndFlush(species);

        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();

        // Update the species using partial update
        Species partialUpdatedSpecies = new Species();
        partialUpdatedSpecies.setId(species.getId());

        partialUpdatedSpecies.name(UPDATED_NAME).commonName(UPDATED_COMMON_NAME).description(UPDATED_DESCRIPTION);

        restSpeciesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSpecies.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSpecies))
            )
            .andExpect(status().isOk());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        Species testSpecies = speciesList.get(speciesList.size() - 1);
        assertThat(testSpecies.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpecies.getCommonName()).isEqualTo(UPDATED_COMMON_NAME);
        assertThat(testSpecies.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingSpecies() throws Exception {
        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        species.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSpeciesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, species.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(species))
            )
            .andExpect(status().isBadRequest());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSpecies() throws Exception {
        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        species.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpeciesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(species))
            )
            .andExpect(status().isBadRequest());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSpecies() throws Exception {
        int databaseSizeBeforeUpdate = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        species.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSpeciesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(species)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Species in the database
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteSpecies() throws Exception {
        // Initialize the database
        speciesRepository.saveAndFlush(species);
        speciesRepository.save(species);
        speciesSearchRepository.save(species);

        int databaseSizeBeforeDelete = speciesRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the species
        restSpeciesMockMvc
            .perform(delete(ENTITY_API_URL_ID, species.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Species> speciesList = speciesRepository.findAll();
        assertThat(speciesList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(speciesSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchSpecies() throws Exception {
        // Initialize the database
        species = speciesRepository.saveAndFlush(species);
        speciesSearchRepository.save(species);

        // Search the species
        restSpeciesMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + species.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(species.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].commonName").value(hasItem(DEFAULT_COMMON_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
