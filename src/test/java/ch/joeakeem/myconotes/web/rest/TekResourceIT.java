package ch.joeakeem.myconotes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.joeakeem.myconotes.IntegrationTest;
import ch.joeakeem.myconotes.domain.Tek;
import ch.joeakeem.myconotes.repository.TekRepository;
import ch.joeakeem.myconotes.repository.search.TekSearchRepository;
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
 * Integration tests for the {@link TekResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TekResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/teks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/teks";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TekRepository tekRepository;

    @Autowired
    private TekSearchRepository tekSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTekMockMvc;

    private Tek tek;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tek createEntity(EntityManager em) {
        Tek tek = new Tek().title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
        return tek;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tek createUpdatedEntity(EntityManager em) {
        Tek tek = new Tek().title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
        return tek;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        tekSearchRepository.deleteAll();
        assertThat(tekSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        tek = createEntity(em);
    }

    @Test
    @Transactional
    void createTek() throws Exception {
        int databaseSizeBeforeCreate = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        // Create the Tek
        restTekMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tek)))
            .andExpect(status().isCreated());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Tek testTek = tekList.get(tekList.size() - 1);
        assertThat(testTek.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTek.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createTekWithExistingId() throws Exception {
        // Create the Tek with an existing ID
        tek.setId(1L);

        int databaseSizeBeforeCreate = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTekMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tek)))
            .andExpect(status().isBadRequest());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        // set the field null
        tek.setTitle(null);

        // Create the Tek, which fails.

        restTekMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tek)))
            .andExpect(status().isBadRequest());

        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTeks() throws Exception {
        // Initialize the database
        tekRepository.saveAndFlush(tek);

        // Get all the tekList
        restTekMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tek.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    void getTek() throws Exception {
        // Initialize the database
        tekRepository.saveAndFlush(tek);

        // Get the tek
        restTekMockMvc
            .perform(get(ENTITY_API_URL_ID, tek.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tek.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTek() throws Exception {
        // Get the tek
        restTekMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTek() throws Exception {
        // Initialize the database
        tekRepository.saveAndFlush(tek);

        int databaseSizeBeforeUpdate = tekRepository.findAll().size();
        tekSearchRepository.save(tek);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());

        // Update the tek
        Tek updatedTek = tekRepository.findById(tek.getId()).get();
        // Disconnect from session so that the updates on updatedTek are not directly saved in db
        em.detach(updatedTek);
        updatedTek.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restTekMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTek.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTek))
            )
            .andExpect(status().isOk());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        Tek testTek = tekList.get(tekList.size() - 1);
        assertThat(testTek.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTek.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Tek> tekSearchList = IterableUtils.toList(tekSearchRepository.findAll());
                Tek testTekSearch = tekSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTekSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testTekSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingTek() throws Exception {
        int databaseSizeBeforeUpdate = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        tek.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTekMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tek.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tek))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTek() throws Exception {
        int databaseSizeBeforeUpdate = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        tek.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTekMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tek))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTek() throws Exception {
        int databaseSizeBeforeUpdate = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        tek.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTekMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tek)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTekWithPatch() throws Exception {
        // Initialize the database
        tekRepository.saveAndFlush(tek);

        int databaseSizeBeforeUpdate = tekRepository.findAll().size();

        // Update the tek using partial update
        Tek partialUpdatedTek = new Tek();
        partialUpdatedTek.setId(tek.getId());

        partialUpdatedTek.title(UPDATED_TITLE);

        restTekMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTek.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTek))
            )
            .andExpect(status().isOk());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        Tek testTek = tekList.get(tekList.size() - 1);
        assertThat(testTek.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTek.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateTekWithPatch() throws Exception {
        // Initialize the database
        tekRepository.saveAndFlush(tek);

        int databaseSizeBeforeUpdate = tekRepository.findAll().size();

        // Update the tek using partial update
        Tek partialUpdatedTek = new Tek();
        partialUpdatedTek.setId(tek.getId());

        partialUpdatedTek.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restTekMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTek.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTek))
            )
            .andExpect(status().isOk());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        Tek testTek = tekList.get(tekList.size() - 1);
        assertThat(testTek.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTek.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingTek() throws Exception {
        int databaseSizeBeforeUpdate = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        tek.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTekMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tek.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tek))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTek() throws Exception {
        int databaseSizeBeforeUpdate = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        tek.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTekMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tek))
            )
            .andExpect(status().isBadRequest());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTek() throws Exception {
        int databaseSizeBeforeUpdate = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        tek.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTekMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tek)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Tek in the database
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTek() throws Exception {
        // Initialize the database
        tekRepository.saveAndFlush(tek);
        tekRepository.save(tek);
        tekSearchRepository.save(tek);

        int databaseSizeBeforeDelete = tekRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the tek
        restTekMockMvc.perform(delete(ENTITY_API_URL_ID, tek.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Tek> tekList = tekRepository.findAll();
        assertThat(tekList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(tekSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTek() throws Exception {
        // Initialize the database
        tek = tekRepository.saveAndFlush(tek);
        tekSearchRepository.save(tek);

        // Search the tek
        restTekMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + tek.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tek.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
