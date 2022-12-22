package ch.joeakeem.myconotes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.joeakeem.myconotes.IntegrationTest;
import ch.joeakeem.myconotes.domain.Experiment;
import ch.joeakeem.myconotes.domain.Species;
import ch.joeakeem.myconotes.domain.Strain;
import ch.joeakeem.myconotes.repository.StrainRepository;
import ch.joeakeem.myconotes.repository.search.StrainSearchRepository;
import ch.joeakeem.myconotes.service.StrainService;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link StrainResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class StrainResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ISOLATED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ISOLATED_AT = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/strains";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/strains";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StrainRepository strainRepository;

    @Mock
    private StrainRepository strainRepositoryMock;

    @Mock
    private StrainService strainServiceMock;

    @Autowired
    private StrainSearchRepository strainSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restStrainMockMvc;

    private Strain strain;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Strain createEntity(EntityManager em) {
        Strain strain = new Strain().name(DEFAULT_NAME).description(DEFAULT_DESCRIPTION).isolatedAt(DEFAULT_ISOLATED_AT);
        // Add required entity
        Species species;
        if (TestUtil.findAll(em, Species.class).isEmpty()) {
            species = SpeciesResourceIT.createEntity(em);
            em.persist(species);
            em.flush();
        } else {
            species = TestUtil.findAll(em, Species.class).get(0);
        }
        strain.setSpecies(species);
        // Add required entity
        Experiment experiment;
        if (TestUtil.findAll(em, Experiment.class).isEmpty()) {
            experiment = ExperimentResourceIT.createEntity(em);
            em.persist(experiment);
            em.flush();
        } else {
            experiment = TestUtil.findAll(em, Experiment.class).get(0);
        }
        strain.setOrigin(experiment);
        return strain;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Strain createUpdatedEntity(EntityManager em) {
        Strain strain = new Strain().name(UPDATED_NAME).description(UPDATED_DESCRIPTION).isolatedAt(UPDATED_ISOLATED_AT);
        // Add required entity
        Species species;
        if (TestUtil.findAll(em, Species.class).isEmpty()) {
            species = SpeciesResourceIT.createUpdatedEntity(em);
            em.persist(species);
            em.flush();
        } else {
            species = TestUtil.findAll(em, Species.class).get(0);
        }
        strain.setSpecies(species);
        // Add required entity
        Experiment experiment;
        if (TestUtil.findAll(em, Experiment.class).isEmpty()) {
            experiment = ExperimentResourceIT.createUpdatedEntity(em);
            em.persist(experiment);
            em.flush();
        } else {
            experiment = TestUtil.findAll(em, Experiment.class).get(0);
        }
        strain.setOrigin(experiment);
        return strain;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        strainSearchRepository.deleteAll();
        assertThat(strainSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        strain = createEntity(em);
    }

    @Test
    @Transactional
    void createStrain() throws Exception {
        int databaseSizeBeforeCreate = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        // Create the Strain
        restStrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(strain)))
            .andExpect(status().isCreated());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Strain testStrain = strainList.get(strainList.size() - 1);
        assertThat(testStrain.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStrain.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testStrain.getIsolatedAt()).isEqualTo(DEFAULT_ISOLATED_AT);
    }

    @Test
    @Transactional
    void createStrainWithExistingId() throws Exception {
        // Create the Strain with an existing ID
        strain.setId(1L);

        int databaseSizeBeforeCreate = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restStrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(strain)))
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        // set the field null
        strain.setName(null);

        // Create the Strain, which fails.

        restStrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(strain)))
            .andExpect(status().isBadRequest());

        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkIsolatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        // set the field null
        strain.setIsolatedAt(null);

        // Create the Strain, which fails.

        restStrainMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(strain)))
            .andExpect(status().isBadRequest());

        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllStrains() throws Exception {
        // Initialize the database
        strainRepository.saveAndFlush(strain);

        // Get all the strainList
        restStrainMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(strain.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isolatedAt").value(hasItem(DEFAULT_ISOLATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStrainsWithEagerRelationshipsIsEnabled() throws Exception {
        when(strainServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStrainMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(strainServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllStrainsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(strainServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restStrainMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(strainRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getStrain() throws Exception {
        // Initialize the database
        strainRepository.saveAndFlush(strain);

        // Get the strain
        restStrainMockMvc
            .perform(get(ENTITY_API_URL_ID, strain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(strain.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.isolatedAt").value(DEFAULT_ISOLATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingStrain() throws Exception {
        // Get the strain
        restStrainMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingStrain() throws Exception {
        // Initialize the database
        strainRepository.saveAndFlush(strain);

        int databaseSizeBeforeUpdate = strainRepository.findAll().size();
        strainSearchRepository.save(strain);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());

        // Update the strain
        Strain updatedStrain = strainRepository.findById(strain.getId()).get();
        // Disconnect from session so that the updates on updatedStrain are not directly saved in db
        em.detach(updatedStrain);
        updatedStrain.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).isolatedAt(UPDATED_ISOLATED_AT);

        restStrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedStrain.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedStrain))
            )
            .andExpect(status().isOk());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        Strain testStrain = strainList.get(strainList.size() - 1);
        assertThat(testStrain.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStrain.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStrain.getIsolatedAt()).isEqualTo(UPDATED_ISOLATED_AT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Strain> strainSearchList = IterableUtils.toList(strainSearchRepository.findAll());
                Strain testStrainSearch = strainSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testStrainSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testStrainSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
                assertThat(testStrainSearch.getIsolatedAt()).isEqualTo(UPDATED_ISOLATED_AT);
            });
    }

    @Test
    @Transactional
    void putNonExistingStrain() throws Exception {
        int databaseSizeBeforeUpdate = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        strain.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, strain.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(strain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchStrain() throws Exception {
        int databaseSizeBeforeUpdate = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        strain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(strain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamStrain() throws Exception {
        int databaseSizeBeforeUpdate = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        strain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(strain)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateStrainWithPatch() throws Exception {
        // Initialize the database
        strainRepository.saveAndFlush(strain);

        int databaseSizeBeforeUpdate = strainRepository.findAll().size();

        // Update the strain using partial update
        Strain partialUpdatedStrain = new Strain();
        partialUpdatedStrain.setId(strain.getId());

        partialUpdatedStrain.description(UPDATED_DESCRIPTION);

        restStrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStrain))
            )
            .andExpect(status().isOk());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        Strain testStrain = strainList.get(strainList.size() - 1);
        assertThat(testStrain.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testStrain.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStrain.getIsolatedAt()).isEqualTo(DEFAULT_ISOLATED_AT);
    }

    @Test
    @Transactional
    void fullUpdateStrainWithPatch() throws Exception {
        // Initialize the database
        strainRepository.saveAndFlush(strain);

        int databaseSizeBeforeUpdate = strainRepository.findAll().size();

        // Update the strain using partial update
        Strain partialUpdatedStrain = new Strain();
        partialUpdatedStrain.setId(strain.getId());

        partialUpdatedStrain.name(UPDATED_NAME).description(UPDATED_DESCRIPTION).isolatedAt(UPDATED_ISOLATED_AT);

        restStrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedStrain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedStrain))
            )
            .andExpect(status().isOk());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        Strain testStrain = strainList.get(strainList.size() - 1);
        assertThat(testStrain.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testStrain.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testStrain.getIsolatedAt()).isEqualTo(UPDATED_ISOLATED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingStrain() throws Exception {
        int databaseSizeBeforeUpdate = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        strain.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, strain.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(strain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchStrain() throws Exception {
        int databaseSizeBeforeUpdate = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        strain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(strain))
            )
            .andExpect(status().isBadRequest());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamStrain() throws Exception {
        int databaseSizeBeforeUpdate = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        strain.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restStrainMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(strain)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Strain in the database
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteStrain() throws Exception {
        // Initialize the database
        strainRepository.saveAndFlush(strain);
        strainRepository.save(strain);
        strainSearchRepository.save(strain);

        int databaseSizeBeforeDelete = strainRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the strain
        restStrainMockMvc
            .perform(delete(ENTITY_API_URL_ID, strain.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Strain> strainList = strainRepository.findAll();
        assertThat(strainList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(strainSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchStrain() throws Exception {
        // Initialize the database
        strain = strainRepository.saveAndFlush(strain);
        strainSearchRepository.save(strain);

        // Search the strain
        restStrainMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + strain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(strain.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].isolatedAt").value(hasItem(DEFAULT_ISOLATED_AT.toString())));
    }
}
