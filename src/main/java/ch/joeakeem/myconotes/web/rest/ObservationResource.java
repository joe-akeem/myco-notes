package ch.joeakeem.myconotes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Observation;
import ch.joeakeem.myconotes.repository.ObservationRepository;
import ch.joeakeem.myconotes.service.ObservationService;
import ch.joeakeem.myconotes.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ch.joeakeem.myconotes.domain.Observation}.
 */
@RestController
@RequestMapping("/api")
public class ObservationResource {

    private final Logger log = LoggerFactory.getLogger(ObservationResource.class);

    private static final String ENTITY_NAME = "observation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ObservationService observationService;

    private final ObservationRepository observationRepository;

    public ObservationResource(ObservationService observationService, ObservationRepository observationRepository) {
        this.observationService = observationService;
        this.observationRepository = observationRepository;
    }

    /**
     * {@code POST  /observations} : Create a new observation.
     *
     * @param observation the observation to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new observation, or with status {@code 400 (Bad Request)} if the observation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/observations")
    public ResponseEntity<Observation> createObservation(@Valid @RequestBody Observation observation) throws URISyntaxException {
        log.debug("REST request to save Observation : {}", observation);
        if (observation.getId() != null) {
            throw new BadRequestAlertException("A new observation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Observation result = observationService.save(observation);
        return ResponseEntity
            .created(new URI("/api/observations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /observations/:id} : Updates an existing observation.
     *
     * @param id the id of the observation to save.
     * @param observation the observation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated observation,
     * or with status {@code 400 (Bad Request)} if the observation is not valid,
     * or with status {@code 500 (Internal Server Error)} if the observation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/observations/{id}")
    public ResponseEntity<Observation> updateObservation(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Observation observation
    ) throws URISyntaxException {
        log.debug("REST request to update Observation : {}, {}", id, observation);
        if (observation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, observation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!observationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Observation result = observationService.update(observation);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, observation.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /observations/:id} : Partial updates given fields of an existing observation, field will ignore if it is null
     *
     * @param id the id of the observation to save.
     * @param observation the observation to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated observation,
     * or with status {@code 400 (Bad Request)} if the observation is not valid,
     * or with status {@code 404 (Not Found)} if the observation is not found,
     * or with status {@code 500 (Internal Server Error)} if the observation couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/observations/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Observation> partialUpdateObservation(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Observation observation
    ) throws URISyntaxException {
        log.debug("REST request to partial update Observation partially : {}, {}", id, observation);
        if (observation.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, observation.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!observationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Observation> result = observationService.partialUpdate(observation);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, observation.getId().toString())
        );
    }

    /**
     * {@code GET  /observations} : get all the observations.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of observations in body.
     */
    @GetMapping("/observations")
    public ResponseEntity<List<Observation>> getAllObservations(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Observations");
        Page<Observation> page;
        if (eagerload) {
            page = observationService.findAllWithEagerRelationships(pageable);
        } else {
            page = observationService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /observations/:id} : get the "id" observation.
     *
     * @param id the id of the observation to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the observation, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/observations/{id}")
    public ResponseEntity<Observation> getObservation(@PathVariable Long id) {
        log.debug("REST request to get Observation : {}", id);
        Optional<Observation> observation = observationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(observation);
    }

    /**
     * {@code DELETE  /observations/:id} : delete the "id" observation.
     *
     * @param id the id of the observation to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/observations/{id}")
    public ResponseEntity<Void> deleteObservation(@PathVariable Long id) {
        log.debug("REST request to delete Observation : {}", id);
        observationService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/observations?query=:query} : search for the observation corresponding
     * to the query.
     *
     * @param query the query of the observation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/observations")
    public ResponseEntity<List<Observation>> searchObservations(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Observations for query {}", query);
        Page<Observation> page = observationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
