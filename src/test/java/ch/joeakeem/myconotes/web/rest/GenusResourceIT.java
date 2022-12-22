package ch.joeakeem.myconotes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.joeakeem.myconotes.IntegrationTest;
import ch.joeakeem.myconotes.domain.Genus;
import ch.joeakeem.myconotes.repository.GenusRepository;
import ch.joeakeem.myconotes.repository.search.GenusSearchRepository;
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
 * Integration tests for the {@link GenusResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GenusResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMMON_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMMON_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/genera";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/genera";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GenusRepository genusRepository;

    @Autowired
    private GenusSearchRepository genusSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGenusMockMvc;

    private Genus genus;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Genus createEntity(EntityManager em) {
        Genus genus = new Genus().name(DEFAULT_NAME).commonName(DEFAULT_COMMON_NAME).description(DEFAULT_DESCRIPTION);
        return genus;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Genus createUpdatedEntity(EntityManager em) {
        Genus genus = new Genus().name(UPDATED_NAME).commonName(UPDATED_COMMON_NAME).description(UPDATED_DESCRIPTION);
        return genus;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        genusSearchRepository.deleteAll();
        assertThat(genusSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        genus = createEntity(em);
    }

    @Test
    @Transactional
    void createGenus() throws Exception {
        int databaseSizeBeforeCreate = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        // Create the Genus
        restGenusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(genus)))
            .andExpect(status().isCreated());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Genus testGenus = genusList.get(genusList.size() - 1);
        assertThat(testGenus.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testGenus.getCommonName()).isEqualTo(DEFAULT_COMMON_NAME);
        assertThat(testGenus.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createGenusWithExistingId() throws Exception {
        // Create the Genus with an existing ID
        genus.setId(1L);

        int databaseSizeBeforeCreate = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restGenusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(genus)))
            .andExpect(status().isBadRequest());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        // set the field null
        genus.setName(null);

        // Create the Genus, which fails.

        restGenusMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(genus)))
            .andExpect(status().isBadRequest());

        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllGenera() throws Exception {
        // Initialize the database
        genusRepository.saveAndFlush(genus);

        // Get all the genusList
        restGenusMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].commonName").value(hasItem(DEFAULT_COMMON_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getGenus() throws Exception {
        // Initialize the database
        genusRepository.saveAndFlush(genus);

        // Get the genus
        restGenusMockMvc
            .perform(get(ENTITY_API_URL_ID, genus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(genus.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.commonName").value(DEFAULT_COMMON_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingGenus() throws Exception {
        // Get the genus
        restGenusMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingGenus() throws Exception {
        // Initialize the database
        genusRepository.saveAndFlush(genus);

        int databaseSizeBeforeUpdate = genusRepository.findAll().size();
        genusSearchRepository.save(genus);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());

        // Update the genus
        Genus updatedGenus = genusRepository.findById(genus.getId()).get();
        // Disconnect from session so that the updates on updatedGenus are not directly saved in db
        em.detach(updatedGenus);
        updatedGenus.name(UPDATED_NAME).commonName(UPDATED_COMMON_NAME).description(UPDATED_DESCRIPTION);

        restGenusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGenus.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGenus))
            )
            .andExpect(status().isOk());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        Genus testGenus = genusList.get(genusList.size() - 1);
        assertThat(testGenus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testGenus.getCommonName()).isEqualTo(UPDATED_COMMON_NAME);
        assertThat(testGenus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Genus> genusSearchList = IterableUtils.toList(genusSearchRepository.findAll());
                Genus testGenusSearch = genusSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testGenusSearch.getName()).isEqualTo(UPDATED_NAME);
                assertThat(testGenusSearch.getCommonName()).isEqualTo(UPDATED_COMMON_NAME);
                assertThat(testGenusSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingGenus() throws Exception {
        int databaseSizeBeforeUpdate = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        genus.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, genus.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(genus))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchGenus() throws Exception {
        int databaseSizeBeforeUpdate = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        genus.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenusMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(genus))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGenus() throws Exception {
        int databaseSizeBeforeUpdate = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        genus.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenusMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(genus)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateGenusWithPatch() throws Exception {
        // Initialize the database
        genusRepository.saveAndFlush(genus);

        int databaseSizeBeforeUpdate = genusRepository.findAll().size();

        // Update the genus using partial update
        Genus partialUpdatedGenus = new Genus();
        partialUpdatedGenus.setId(genus.getId());

        partialUpdatedGenus.name(UPDATED_NAME).commonName(UPDATED_COMMON_NAME).description(UPDATED_DESCRIPTION);

        restGenusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenus.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGenus))
            )
            .andExpect(status().isOk());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        Genus testGenus = genusList.get(genusList.size() - 1);
        assertThat(testGenus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testGenus.getCommonName()).isEqualTo(UPDATED_COMMON_NAME);
        assertThat(testGenus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateGenusWithPatch() throws Exception {
        // Initialize the database
        genusRepository.saveAndFlush(genus);

        int databaseSizeBeforeUpdate = genusRepository.findAll().size();

        // Update the genus using partial update
        Genus partialUpdatedGenus = new Genus();
        partialUpdatedGenus.setId(genus.getId());

        partialUpdatedGenus.name(UPDATED_NAME).commonName(UPDATED_COMMON_NAME).description(UPDATED_DESCRIPTION);

        restGenusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGenus.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGenus))
            )
            .andExpect(status().isOk());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        Genus testGenus = genusList.get(genusList.size() - 1);
        assertThat(testGenus.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testGenus.getCommonName()).isEqualTo(UPDATED_COMMON_NAME);
        assertThat(testGenus.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingGenus() throws Exception {
        int databaseSizeBeforeUpdate = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        genus.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGenusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, genus.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(genus))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGenus() throws Exception {
        int databaseSizeBeforeUpdate = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        genus.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenusMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(genus))
            )
            .andExpect(status().isBadRequest());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGenus() throws Exception {
        int databaseSizeBeforeUpdate = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        genus.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGenusMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(genus)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Genus in the database
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteGenus() throws Exception {
        // Initialize the database
        genusRepository.saveAndFlush(genus);
        genusRepository.save(genus);
        genusSearchRepository.save(genus);

        int databaseSizeBeforeDelete = genusRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the genus
        restGenusMockMvc
            .perform(delete(ENTITY_API_URL_ID, genus.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Genus> genusList = genusRepository.findAll();
        assertThat(genusList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(genusSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchGenus() throws Exception {
        // Initialize the database
        genus = genusRepository.saveAndFlush(genus);
        genusSearchRepository.save(genus);

        // Search the genus
        restGenusMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + genus.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(genus.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].commonName").value(hasItem(DEFAULT_COMMON_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
