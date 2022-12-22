package ch.joeakeem.myconotes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.joeakeem.myconotes.IntegrationTest;
import ch.joeakeem.myconotes.domain.Experiment;
import ch.joeakeem.myconotes.domain.Tek;
import ch.joeakeem.myconotes.domain.User;
import ch.joeakeem.myconotes.repository.ExperimentRepository;
import ch.joeakeem.myconotes.repository.search.ExperimentSearchRepository;
import ch.joeakeem.myconotes.service.ExperimentService;
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
 * Integration tests for the {@link ExperimentResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ExperimentResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_CONDUCTED_AT = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CONDUCTED_AT = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/experiments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/experiments";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ExperimentRepository experimentRepository;

    @Mock
    private ExperimentRepository experimentRepositoryMock;

    @Mock
    private ExperimentService experimentServiceMock;

    @Autowired
    private ExperimentSearchRepository experimentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restExperimentMockMvc;

    private Experiment experiment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Experiment createEntity(EntityManager em) {
        Experiment experiment = new Experiment().title(DEFAULT_TITLE).notes(DEFAULT_NOTES).conductedAt(DEFAULT_CONDUCTED_AT);
        // Add required entity
        Tek tek;
        if (TestUtil.findAll(em, Tek.class).isEmpty()) {
            tek = TekResourceIT.createEntity(em);
            em.persist(tek);
            em.flush();
        } else {
            tek = TestUtil.findAll(em, Tek.class).get(0);
        }
        experiment.setTek(tek);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        experiment.setConductedBy(user);
        return experiment;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Experiment createUpdatedEntity(EntityManager em) {
        Experiment experiment = new Experiment().title(UPDATED_TITLE).notes(UPDATED_NOTES).conductedAt(UPDATED_CONDUCTED_AT);
        // Add required entity
        Tek tek;
        if (TestUtil.findAll(em, Tek.class).isEmpty()) {
            tek = TekResourceIT.createUpdatedEntity(em);
            em.persist(tek);
            em.flush();
        } else {
            tek = TestUtil.findAll(em, Tek.class).get(0);
        }
        experiment.setTek(tek);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        experiment.setConductedBy(user);
        return experiment;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        experimentSearchRepository.deleteAll();
        assertThat(experimentSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        experiment = createEntity(em);
    }

    @Test
    @Transactional
    void createExperiment() throws Exception {
        int databaseSizeBeforeCreate = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        // Create the Experiment
        restExperimentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiment)))
            .andExpect(status().isCreated());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Experiment testExperiment = experimentList.get(experimentList.size() - 1);
        assertThat(testExperiment.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testExperiment.getNotes()).isEqualTo(DEFAULT_NOTES);
        assertThat(testExperiment.getConductedAt()).isEqualTo(DEFAULT_CONDUCTED_AT);
    }

    @Test
    @Transactional
    void createExperimentWithExistingId() throws Exception {
        // Create the Experiment with an existing ID
        experiment.setId(1L);

        int databaseSizeBeforeCreate = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restExperimentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiment)))
            .andExpect(status().isBadRequest());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        // set the field null
        experiment.setTitle(null);

        // Create the Experiment, which fails.

        restExperimentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiment)))
            .andExpect(status().isBadRequest());

        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkConductedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        // set the field null
        experiment.setConductedAt(null);

        // Create the Experiment, which fails.

        restExperimentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiment)))
            .andExpect(status().isBadRequest());

        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllExperiments() throws Exception {
        // Initialize the database
        experimentRepository.saveAndFlush(experiment);

        // Get all the experimentList
        restExperimentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(experiment.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES.toString())))
            .andExpect(jsonPath("$.[*].conductedAt").value(hasItem(DEFAULT_CONDUCTED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExperimentsWithEagerRelationshipsIsEnabled() throws Exception {
        when(experimentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExperimentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(experimentServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllExperimentsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(experimentServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restExperimentMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(experimentRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getExperiment() throws Exception {
        // Initialize the database
        experimentRepository.saveAndFlush(experiment);

        // Get the experiment
        restExperimentMockMvc
            .perform(get(ENTITY_API_URL_ID, experiment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(experiment.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES.toString()))
            .andExpect(jsonPath("$.conductedAt").value(DEFAULT_CONDUCTED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingExperiment() throws Exception {
        // Get the experiment
        restExperimentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingExperiment() throws Exception {
        // Initialize the database
        experimentRepository.saveAndFlush(experiment);

        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();
        experimentSearchRepository.save(experiment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());

        // Update the experiment
        Experiment updatedExperiment = experimentRepository.findById(experiment.getId()).get();
        // Disconnect from session so that the updates on updatedExperiment are not directly saved in db
        em.detach(updatedExperiment);
        updatedExperiment.title(UPDATED_TITLE).notes(UPDATED_NOTES).conductedAt(UPDATED_CONDUCTED_AT);

        restExperimentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedExperiment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedExperiment))
            )
            .andExpect(status().isOk());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        Experiment testExperiment = experimentList.get(experimentList.size() - 1);
        assertThat(testExperiment.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testExperiment.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testExperiment.getConductedAt()).isEqualTo(UPDATED_CONDUCTED_AT);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Experiment> experimentSearchList = IterableUtils.toList(experimentSearchRepository.findAll());
                Experiment testExperimentSearch = experimentSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testExperimentSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testExperimentSearch.getNotes()).isEqualTo(UPDATED_NOTES);
                assertThat(testExperimentSearch.getConductedAt()).isEqualTo(UPDATED_CONDUCTED_AT);
            });
    }

    @Test
    @Transactional
    void putNonExistingExperiment() throws Exception {
        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        experiment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExperimentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, experiment.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(experiment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchExperiment() throws Exception {
        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        experiment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperimentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(experiment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamExperiment() throws Exception {
        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        experiment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperimentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(experiment)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateExperimentWithPatch() throws Exception {
        // Initialize the database
        experimentRepository.saveAndFlush(experiment);

        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();

        // Update the experiment using partial update
        Experiment partialUpdatedExperiment = new Experiment();
        partialUpdatedExperiment.setId(experiment.getId());

        partialUpdatedExperiment.notes(UPDATED_NOTES).conductedAt(UPDATED_CONDUCTED_AT);

        restExperimentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExperiment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExperiment))
            )
            .andExpect(status().isOk());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        Experiment testExperiment = experimentList.get(experimentList.size() - 1);
        assertThat(testExperiment.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testExperiment.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testExperiment.getConductedAt()).isEqualTo(UPDATED_CONDUCTED_AT);
    }

    @Test
    @Transactional
    void fullUpdateExperimentWithPatch() throws Exception {
        // Initialize the database
        experimentRepository.saveAndFlush(experiment);

        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();

        // Update the experiment using partial update
        Experiment partialUpdatedExperiment = new Experiment();
        partialUpdatedExperiment.setId(experiment.getId());

        partialUpdatedExperiment.title(UPDATED_TITLE).notes(UPDATED_NOTES).conductedAt(UPDATED_CONDUCTED_AT);

        restExperimentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedExperiment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedExperiment))
            )
            .andExpect(status().isOk());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        Experiment testExperiment = experimentList.get(experimentList.size() - 1);
        assertThat(testExperiment.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testExperiment.getNotes()).isEqualTo(UPDATED_NOTES);
        assertThat(testExperiment.getConductedAt()).isEqualTo(UPDATED_CONDUCTED_AT);
    }

    @Test
    @Transactional
    void patchNonExistingExperiment() throws Exception {
        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        experiment.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restExperimentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, experiment.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(experiment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchExperiment() throws Exception {
        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        experiment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperimentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(experiment))
            )
            .andExpect(status().isBadRequest());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamExperiment() throws Exception {
        int databaseSizeBeforeUpdate = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        experiment.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restExperimentMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(experiment))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Experiment in the database
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteExperiment() throws Exception {
        // Initialize the database
        experimentRepository.saveAndFlush(experiment);
        experimentRepository.save(experiment);
        experimentSearchRepository.save(experiment);

        int databaseSizeBeforeDelete = experimentRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the experiment
        restExperimentMockMvc
            .perform(delete(ENTITY_API_URL_ID, experiment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Experiment> experimentList = experimentRepository.findAll();
        assertThat(experimentList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(experimentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchExperiment() throws Exception {
        // Initialize the database
        experiment = experimentRepository.saveAndFlush(experiment);
        experimentSearchRepository.save(experiment);

        // Search the experiment
        restExperimentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + experiment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(experiment.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES.toString())))
            .andExpect(jsonPath("$.[*].conductedAt").value(hasItem(DEFAULT_CONDUCTED_AT.toString())));
    }
}
