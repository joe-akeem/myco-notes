package ch.joeakeem.myconotes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Strain;
import ch.joeakeem.myconotes.repository.StrainRepository;
import ch.joeakeem.myconotes.service.StrainService;
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
 * REST controller for managing {@link ch.joeakeem.myconotes.domain.Strain}.
 */
@RestController
@RequestMapping("/api")
public class StrainResource {

    private final Logger log = LoggerFactory.getLogger(StrainResource.class);

    private static final String ENTITY_NAME = "strain";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final StrainService strainService;

    private final StrainRepository strainRepository;

    public StrainResource(StrainService strainService, StrainRepository strainRepository) {
        this.strainService = strainService;
        this.strainRepository = strainRepository;
    }

    /**
     * {@code POST  /strains} : Create a new strain.
     *
     * @param strain the strain to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new strain, or with status {@code 400 (Bad Request)} if the strain has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/strains")
    public ResponseEntity<Strain> createStrain(@Valid @RequestBody Strain strain) throws URISyntaxException {
        log.debug("REST request to save Strain : {}", strain);
        if (strain.getId() != null) {
            throw new BadRequestAlertException("A new strain cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Strain result = strainService.save(strain);
        return ResponseEntity
            .created(new URI("/api/strains/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /strains/:id} : Updates an existing strain.
     *
     * @param id the id of the strain to save.
     * @param strain the strain to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated strain,
     * or with status {@code 400 (Bad Request)} if the strain is not valid,
     * or with status {@code 500 (Internal Server Error)} if the strain couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/strains/{id}")
    public ResponseEntity<Strain> updateStrain(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Strain strain
    ) throws URISyntaxException {
        log.debug("REST request to update Strain : {}, {}", id, strain);
        if (strain.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, strain.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!strainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Strain result = strainService.update(strain);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, strain.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /strains/:id} : Partial updates given fields of an existing strain, field will ignore if it is null
     *
     * @param id the id of the strain to save.
     * @param strain the strain to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated strain,
     * or with status {@code 400 (Bad Request)} if the strain is not valid,
     * or with status {@code 404 (Not Found)} if the strain is not found,
     * or with status {@code 500 (Internal Server Error)} if the strain couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/strains/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Strain> partialUpdateStrain(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Strain strain
    ) throws URISyntaxException {
        log.debug("REST request to partial update Strain partially : {}, {}", id, strain);
        if (strain.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, strain.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!strainRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Strain> result = strainService.partialUpdate(strain);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, strain.getId().toString())
        );
    }

    /**
     * {@code GET  /strains} : get all the strains.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of strains in body.
     */
    @GetMapping("/strains")
    public ResponseEntity<List<Strain>> getAllStrains(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Strains");
        Page<Strain> page;
        if (eagerload) {
            page = strainService.findAllWithEagerRelationships(pageable);
        } else {
            page = strainService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /strains/:id} : get the "id" strain.
     *
     * @param id the id of the strain to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the strain, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/strains/{id}")
    public ResponseEntity<Strain> getStrain(@PathVariable Long id) {
        log.debug("REST request to get Strain : {}", id);
        Optional<Strain> strain = strainService.findOne(id);
        return ResponseUtil.wrapOrNotFound(strain);
    }

    /**
     * {@code DELETE  /strains/:id} : delete the "id" strain.
     *
     * @param id the id of the strain to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/strains/{id}")
    public ResponseEntity<Void> deleteStrain(@PathVariable Long id) {
        log.debug("REST request to delete Strain : {}", id);
        strainService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/strains?query=:query} : search for the strain corresponding
     * to the query.
     *
     * @param query the query of the strain search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/strains")
    public ResponseEntity<List<Strain>> searchStrains(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Strains for query {}", query);
        Page<Strain> page = strainService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
