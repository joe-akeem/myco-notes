package ch.joeakeem.myconotes.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.joeakeem.myconotes.IntegrationTest;
import ch.joeakeem.myconotes.domain.Experiment;
import ch.joeakeem.myconotes.domain.Image;
import ch.joeakeem.myconotes.domain.Observation;
import ch.joeakeem.myconotes.repository.ObservationRepository;
import ch.joeakeem.myconotes.repository.search.ObservationSearchRepository;
import ch.joeakeem.myconotes.service.ObservationService;
import ch.joeakeem.myconotes.service.criteria.ObservationCriteria;
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
 * Integration tests for the {@link ObservationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class ObservationResourceIT {

    private static final LocalDate DEFAULT_OBSERVATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_OBSERVATION_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_OBSERVATION_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/observations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/observations";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObservationRepository observationRepository;

    @Mock
    private ObservationRepository observationRepositoryMock;

    @Mock
    private ObservationService observationServiceMock;

    @Autowired
    private ObservationSearchRepository observationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restObservationMockMvc;

    private Observation observation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Observation createEntity(EntityManager em) {
        Observation observation = new Observation()
            .observationDate(DEFAULT_OBSERVATION_DATE)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION);
        // Add required entity
        Experiment experiment;
        if (TestUtil.findAll(em, Experiment.class).isEmpty()) {
            experiment = ExperimentResourceIT.createEntity(em);
            em.persist(experiment);
            em.flush();
        } else {
            experiment = TestUtil.findAll(em, Experiment.class).get(0);
        }
        observation.setExperiment(experiment);
        return observation;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Observation createUpdatedEntity(EntityManager em) {
        Observation observation = new Observation()
            .observationDate(UPDATED_OBSERVATION_DATE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION);
        // Add required entity
        Experiment experiment;
        if (TestUtil.findAll(em, Experiment.class).isEmpty()) {
            experiment = ExperimentResourceIT.createUpdatedEntity(em);
            em.persist(experiment);
            em.flush();
        } else {
            experiment = TestUtil.findAll(em, Experiment.class).get(0);
        }
        observation.setExperiment(experiment);
        return observation;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        observationSearchRepository.deleteAll();
        assertThat(observationSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        observation = createEntity(em);
    }

    @Test
    @Transactional
    void createObservation() throws Exception {
        int databaseSizeBeforeCreate = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        // Create the Observation
        restObservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(observation)))
            .andExpect(status().isCreated());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Observation testObservation = observationList.get(observationList.size() - 1);
        assertThat(testObservation.getObservationDate()).isEqualTo(DEFAULT_OBSERVATION_DATE);
        assertThat(testObservation.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testObservation.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createObservationWithExistingId() throws Exception {
        // Create the Observation with an existing ID
        observation.setId(1L);

        int databaseSizeBeforeCreate = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restObservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(observation)))
            .andExpect(status().isBadRequest());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkObservationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        // set the field null
        observation.setObservationDate(null);

        // Create the Observation, which fails.

        restObservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(observation)))
            .andExpect(status().isBadRequest());

        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        // set the field null
        observation.setTitle(null);

        // Create the Observation, which fails.

        restObservationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(observation)))
            .andExpect(status().isBadRequest());

        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllObservations() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList
        restObservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(observation.getId().intValue())))
            .andExpect(jsonPath("$.[*].observationDate").value(hasItem(DEFAULT_OBSERVATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllObservationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(observationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restObservationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(observationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllObservationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(observationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restObservationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(observationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getObservation() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get the observation
        restObservationMockMvc
            .perform(get(ENTITY_API_URL_ID, observation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(observation.getId().intValue()))
            .andExpect(jsonPath("$.observationDate").value(DEFAULT_OBSERVATION_DATE.toString()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    void getObservationsByIdFiltering() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        Long id = observation.getId();

        defaultObservationShouldBeFound("id.equals=" + id);
        defaultObservationShouldNotBeFound("id.notEquals=" + id);

        defaultObservationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultObservationShouldNotBeFound("id.greaterThan=" + id);

        defaultObservationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultObservationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllObservationsByObservationDateIsEqualToSomething() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where observationDate equals to DEFAULT_OBSERVATION_DATE
        defaultObservationShouldBeFound("observationDate.equals=" + DEFAULT_OBSERVATION_DATE);

        // Get all the observationList where observationDate equals to UPDATED_OBSERVATION_DATE
        defaultObservationShouldNotBeFound("observationDate.equals=" + UPDATED_OBSERVATION_DATE);
    }

    @Test
    @Transactional
    void getAllObservationsByObservationDateIsInShouldWork() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where observationDate in DEFAULT_OBSERVATION_DATE or UPDATED_OBSERVATION_DATE
        defaultObservationShouldBeFound("observationDate.in=" + DEFAULT_OBSERVATION_DATE + "," + UPDATED_OBSERVATION_DATE);

        // Get all the observationList where observationDate equals to UPDATED_OBSERVATION_DATE
        defaultObservationShouldNotBeFound("observationDate.in=" + UPDATED_OBSERVATION_DATE);
    }

    @Test
    @Transactional
    void getAllObservationsByObservationDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where observationDate is not null
        defaultObservationShouldBeFound("observationDate.specified=true");

        // Get all the observationList where observationDate is null
        defaultObservationShouldNotBeFound("observationDate.specified=false");
    }

    @Test
    @Transactional
    void getAllObservationsByObservationDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where observationDate is greater than or equal to DEFAULT_OBSERVATION_DATE
        defaultObservationShouldBeFound("observationDate.greaterThanOrEqual=" + DEFAULT_OBSERVATION_DATE);

        // Get all the observationList where observationDate is greater than or equal to UPDATED_OBSERVATION_DATE
        defaultObservationShouldNotBeFound("observationDate.greaterThanOrEqual=" + UPDATED_OBSERVATION_DATE);
    }

    @Test
    @Transactional
    void getAllObservationsByObservationDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where observationDate is less than or equal to DEFAULT_OBSERVATION_DATE
        defaultObservationShouldBeFound("observationDate.lessThanOrEqual=" + DEFAULT_OBSERVATION_DATE);

        // Get all the observationList where observationDate is less than or equal to SMALLER_OBSERVATION_DATE
        defaultObservationShouldNotBeFound("observationDate.lessThanOrEqual=" + SMALLER_OBSERVATION_DATE);
    }

    @Test
    @Transactional
    void getAllObservationsByObservationDateIsLessThanSomething() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where observationDate is less than DEFAULT_OBSERVATION_DATE
        defaultObservationShouldNotBeFound("observationDate.lessThan=" + DEFAULT_OBSERVATION_DATE);

        // Get all the observationList where observationDate is less than UPDATED_OBSERVATION_DATE
        defaultObservationShouldBeFound("observationDate.lessThan=" + UPDATED_OBSERVATION_DATE);
    }

    @Test
    @Transactional
    void getAllObservationsByObservationDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where observationDate is greater than DEFAULT_OBSERVATION_DATE
        defaultObservationShouldNotBeFound("observationDate.greaterThan=" + DEFAULT_OBSERVATION_DATE);

        // Get all the observationList where observationDate is greater than SMALLER_OBSERVATION_DATE
        defaultObservationShouldBeFound("observationDate.greaterThan=" + SMALLER_OBSERVATION_DATE);
    }

    @Test
    @Transactional
    void getAllObservationsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where title equals to DEFAULT_TITLE
        defaultObservationShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the observationList where title equals to UPDATED_TITLE
        defaultObservationShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllObservationsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultObservationShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the observationList where title equals to UPDATED_TITLE
        defaultObservationShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllObservationsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where title is not null
        defaultObservationShouldBeFound("title.specified=true");

        // Get all the observationList where title is null
        defaultObservationShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllObservationsByTitleContainsSomething() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where title contains DEFAULT_TITLE
        defaultObservationShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the observationList where title contains UPDATED_TITLE
        defaultObservationShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllObservationsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        // Get all the observationList where title does not contain DEFAULT_TITLE
        defaultObservationShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the observationList where title does not contain UPDATED_TITLE
        defaultObservationShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllObservationsByImagesIsEqualToSomething() throws Exception {
        Image images;
        if (TestUtil.findAll(em, Image.class).isEmpty()) {
            observationRepository.saveAndFlush(observation);
            images = ImageResourceIT.createEntity(em);
        } else {
            images = TestUtil.findAll(em, Image.class).get(0);
        }
        em.persist(images);
        em.flush();
        observation.addImages(images);
        observationRepository.saveAndFlush(observation);
        Long imagesId = images.getId();

        // Get all the observationList where images equals to imagesId
        defaultObservationShouldBeFound("imagesId.equals=" + imagesId);

        // Get all the observationList where images equals to (imagesId + 1)
        defaultObservationShouldNotBeFound("imagesId.equals=" + (imagesId + 1));
    }

    @Test
    @Transactional
    void getAllObservationsByExperimentIsEqualToSomething() throws Exception {
        Experiment experiment;
        if (TestUtil.findAll(em, Experiment.class).isEmpty()) {
            observationRepository.saveAndFlush(observation);
            experiment = ExperimentResourceIT.createEntity(em);
        } else {
            experiment = TestUtil.findAll(em, Experiment.class).get(0);
        }
        em.persist(experiment);
        em.flush();
        observation.setExperiment(experiment);
        observationRepository.saveAndFlush(observation);
        Long experimentId = experiment.getId();

        // Get all the observationList where experiment equals to experimentId
        defaultObservationShouldBeFound("experimentId.equals=" + experimentId);

        // Get all the observationList where experiment equals to (experimentId + 1)
        defaultObservationShouldNotBeFound("experimentId.equals=" + (experimentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultObservationShouldBeFound(String filter) throws Exception {
        restObservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(observation.getId().intValue())))
            .andExpect(jsonPath("$.[*].observationDate").value(hasItem(DEFAULT_OBSERVATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));

        // Check, that the count call also returns 1
        restObservationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultObservationShouldNotBeFound(String filter) throws Exception {
        restObservationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restObservationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingObservation() throws Exception {
        // Get the observation
        restObservationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingObservation() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        int databaseSizeBeforeUpdate = observationRepository.findAll().size();
        observationSearchRepository.save(observation);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());

        // Update the observation
        Observation updatedObservation = observationRepository.findById(observation.getId()).get();
        // Disconnect from session so that the updates on updatedObservation are not directly saved in db
        em.detach(updatedObservation);
        updatedObservation.observationDate(UPDATED_OBSERVATION_DATE).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restObservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedObservation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedObservation))
            )
            .andExpect(status().isOk());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        Observation testObservation = observationList.get(observationList.size() - 1);
        assertThat(testObservation.getObservationDate()).isEqualTo(UPDATED_OBSERVATION_DATE);
        assertThat(testObservation.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testObservation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Observation> observationSearchList = IterableUtils.toList(observationSearchRepository.findAll());
                Observation testObservationSearch = observationSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testObservationSearch.getObservationDate()).isEqualTo(UPDATED_OBSERVATION_DATE);
                assertThat(testObservationSearch.getTitle()).isEqualTo(UPDATED_TITLE);
                assertThat(testObservationSearch.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
            });
    }

    @Test
    @Transactional
    void putNonExistingObservation() throws Exception {
        int databaseSizeBeforeUpdate = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        observation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restObservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, observation.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(observation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchObservation() throws Exception {
        int databaseSizeBeforeUpdate = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        observation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restObservationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(observation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamObservation() throws Exception {
        int databaseSizeBeforeUpdate = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        observation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restObservationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(observation)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateObservationWithPatch() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        int databaseSizeBeforeUpdate = observationRepository.findAll().size();

        // Update the observation using partial update
        Observation partialUpdatedObservation = new Observation();
        partialUpdatedObservation.setId(observation.getId());

        partialUpdatedObservation.title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restObservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedObservation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedObservation))
            )
            .andExpect(status().isOk());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        Observation testObservation = observationList.get(observationList.size() - 1);
        assertThat(testObservation.getObservationDate()).isEqualTo(DEFAULT_OBSERVATION_DATE);
        assertThat(testObservation.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testObservation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateObservationWithPatch() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);

        int databaseSizeBeforeUpdate = observationRepository.findAll().size();

        // Update the observation using partial update
        Observation partialUpdatedObservation = new Observation();
        partialUpdatedObservation.setId(observation.getId());

        partialUpdatedObservation.observationDate(UPDATED_OBSERVATION_DATE).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restObservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedObservation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedObservation))
            )
            .andExpect(status().isOk());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        Observation testObservation = observationList.get(observationList.size() - 1);
        assertThat(testObservation.getObservationDate()).isEqualTo(UPDATED_OBSERVATION_DATE);
        assertThat(testObservation.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testObservation.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingObservation() throws Exception {
        int databaseSizeBeforeUpdate = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        observation.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restObservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, observation.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(observation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchObservation() throws Exception {
        int databaseSizeBeforeUpdate = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        observation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restObservationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(observation))
            )
            .andExpect(status().isBadRequest());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamObservation() throws Exception {
        int databaseSizeBeforeUpdate = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        observation.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restObservationMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(observation))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Observation in the database
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteObservation() throws Exception {
        // Initialize the database
        observationRepository.saveAndFlush(observation);
        observationRepository.save(observation);
        observationSearchRepository.save(observation);

        int databaseSizeBeforeDelete = observationRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the observation
        restObservationMockMvc
            .perform(delete(ENTITY_API_URL_ID, observation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Observation> observationList = observationRepository.findAll();
        assertThat(observationList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(observationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchObservation() throws Exception {
        // Initialize the database
        observation = observationRepository.saveAndFlush(observation);
        observationSearchRepository.save(observation);

        // Search the observation
        restObservationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + observation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(observation.getId().intValue())))
            .andExpect(jsonPath("$.[*].observationDate").value(hasItem(DEFAULT_OBSERVATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }
}
