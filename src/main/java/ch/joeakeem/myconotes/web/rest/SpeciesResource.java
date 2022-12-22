package ch.joeakeem.myconotes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Species;
import ch.joeakeem.myconotes.repository.SpeciesRepository;
import ch.joeakeem.myconotes.service.SpeciesService;
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
 * REST controller for managing {@link ch.joeakeem.myconotes.domain.Species}.
 */
@RestController
@RequestMapping("/api")
public class SpeciesResource {

    private final Logger log = LoggerFactory.getLogger(SpeciesResource.class);

    private static final String ENTITY_NAME = "species";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SpeciesService speciesService;

    private final SpeciesRepository speciesRepository;

    public SpeciesResource(SpeciesService speciesService, SpeciesRepository speciesRepository) {
        this.speciesService = speciesService;
        this.speciesRepository = speciesRepository;
    }

    /**
     * {@code POST  /species} : Create a new species.
     *
     * @param species the species to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new species, or with status {@code 400 (Bad Request)} if the species has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/species")
    public ResponseEntity<Species> createSpecies(@Valid @RequestBody Species species) throws URISyntaxException {
        log.debug("REST request to save Species : {}", species);
        if (species.getId() != null) {
            throw new BadRequestAlertException("A new species cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Species result = speciesService.save(species);
        return ResponseEntity
            .created(new URI("/api/species/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /species/:id} : Updates an existing species.
     *
     * @param id the id of the species to save.
     * @param species the species to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated species,
     * or with status {@code 400 (Bad Request)} if the species is not valid,
     * or with status {@code 500 (Internal Server Error)} if the species couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/species/{id}")
    public ResponseEntity<Species> updateSpecies(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Species species
    ) throws URISyntaxException {
        log.debug("REST request to update Species : {}, {}", id, species);
        if (species.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, species.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!speciesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Species result = speciesService.update(species);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, species.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /species/:id} : Partial updates given fields of an existing species, field will ignore if it is null
     *
     * @param id the id of the species to save.
     * @param species the species to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated species,
     * or with status {@code 400 (Bad Request)} if the species is not valid,
     * or with status {@code 404 (Not Found)} if the species is not found,
     * or with status {@code 500 (Internal Server Error)} if the species couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/species/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Species> partialUpdateSpecies(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Species species
    ) throws URISyntaxException {
        log.debug("REST request to partial update Species partially : {}, {}", id, species);
        if (species.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, species.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!speciesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Species> result = speciesService.partialUpdate(species);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, species.getId().toString())
        );
    }

    /**
     * {@code GET  /species} : get all the species.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of species in body.
     */
    @GetMapping("/species")
    public ResponseEntity<List<Species>> getAllSpecies(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Species");
        Page<Species> page;
        if (eagerload) {
            page = speciesService.findAllWithEagerRelationships(pageable);
        } else {
            page = speciesService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /species/:id} : get the "id" species.
     *
     * @param id the id of the species to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the species, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/species/{id}")
    public ResponseEntity<Species> getSpecies(@PathVariable Long id) {
        log.debug("REST request to get Species : {}", id);
        Optional<Species> species = speciesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(species);
    }

    /**
     * {@code DELETE  /species/:id} : delete the "id" species.
     *
     * @param id the id of the species to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/species/{id}")
    public ResponseEntity<Void> deleteSpecies(@PathVariable Long id) {
        log.debug("REST request to delete Species : {}", id);
        speciesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/species?query=:query} : search for the species corresponding
     * to the query.
     *
     * @param query the query of the species search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/species")
    public ResponseEntity<List<Species>> searchSpecies(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Species for query {}", query);
        Page<Species> page = speciesService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
