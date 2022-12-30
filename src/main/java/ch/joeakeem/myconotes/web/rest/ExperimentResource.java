package ch.joeakeem.myconotes.web.rest;

import ch.joeakeem.myconotes.domain.Experiment;
import ch.joeakeem.myconotes.domain.Row;
import ch.joeakeem.myconotes.repository.ExperimentRepository;
import ch.joeakeem.myconotes.service.ExperimentService;
import ch.joeakeem.myconotes.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ch.joeakeem.myconotes.domain.Experiment}.
 */
@RestController
@RequestMapping("/api")
public class ExperimentResource {

    private final Logger log = LoggerFactory.getLogger(ExperimentResource.class);

    private static final String ENTITY_NAME = "experiment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExperimentService experimentService;

    private final ExperimentRepository experimentRepository;

    public ExperimentResource(ExperimentService experimentService, ExperimentRepository experimentRepository) {
        this.experimentService = experimentService;
        this.experimentRepository = experimentRepository;
    }

    /**
     * {@code POST  /experiments} : Create a new experiment.
     *
     * @param experiment the experiment to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new experiment, or with status {@code 400 (Bad Request)} if the experiment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/experiments")
    public ResponseEntity<Experiment> createExperiment(@Valid @RequestBody Experiment experiment) throws URISyntaxException {
        log.debug("REST request to save Experiment : {}", experiment);
        if (experiment.getId() != null) {
            throw new BadRequestAlertException("A new experiment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Experiment result = experimentService.save(experiment);
        return ResponseEntity
            .created(new URI("/api/experiments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /experiments/:id} : Updates an existing experiment.
     *
     * @param id the id of the experiment to save.
     * @param experiment the experiment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated experiment,
     * or with status {@code 400 (Bad Request)} if the experiment is not valid,
     * or with status {@code 500 (Internal Server Error)} if the experiment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/experiments/{id}")
    public ResponseEntity<Experiment> updateExperiment(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Experiment experiment
    ) throws URISyntaxException {
        log.debug("REST request to update Experiment : {}, {}", id, experiment);
        if (experiment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, experiment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!experimentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Experiment result = experimentService.update(experiment);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, experiment.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /experiments/:id} : Partial updates given fields of an existing experiment, field will ignore if it is null
     *
     * @param id the id of the experiment to save.
     * @param experiment the experiment to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated experiment,
     * or with status {@code 400 (Bad Request)} if the experiment is not valid,
     * or with status {@code 404 (Not Found)} if the experiment is not found,
     * or with status {@code 500 (Internal Server Error)} if the experiment couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/experiments/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Experiment> partialUpdateExperiment(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Experiment experiment
    ) throws URISyntaxException {
        log.debug("REST request to partial update Experiment partially : {}, {}", id, experiment);
        if (experiment.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, experiment.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!experimentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Experiment> result = experimentService.partialUpdate(experiment);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, experiment.getId().toString())
        );
    }

    /**
     * {@code GET  /experiments} : get all the experiments.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of experiments in body.
     */
    @GetMapping("/experiments")
    public ResponseEntity<List<Experiment>> getAllExperiments(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Experiments");
        Page<Experiment> page;
        if (eagerload) {
            page = experimentService.findAllWithEagerRelationships(pageable);
        } else {
            page = experimentService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /experiments/:id} : get the "id" experiment.
     *
     * @param id the id of the experiment to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the experiment, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/experiments/{id}")
    public ResponseEntity<Experiment> getExperiment(@PathVariable Long id) {
        log.debug("REST request to get Experiment : {}", id);
        Optional<Experiment> experiment = experimentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(experiment);
    }

    @GetMapping("/experiments/{id}/sankeyChartData")
    public ResponseEntity<List<Row>> getSankeyChartData(@PathVariable Long id) {
        log.debug("REST request to get Sankey chart data for Experiment : {}", id);
        final List<Row> chartData = experimentService.getChartData(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(chartData);
    }

    /**
     * {@code DELETE  /experiments/:id} : delete the "id" experiment.
     *
     * @param id the id of the experiment to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/experiments/{id}")
    public ResponseEntity<Void> deleteExperiment(@PathVariable Long id) {
        log.debug("REST request to delete Experiment : {}", id);
        experimentService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/experiments?query=:query} : search for the experiment corresponding
     * to the query.
     *
     * @param query the query of the experiment search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/experiments")
    public ResponseEntity<List<Experiment>> searchExperiments(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Experiments for query {}", query);
        Page<Experiment> page = experimentService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
