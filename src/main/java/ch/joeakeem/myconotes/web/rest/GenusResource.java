package ch.joeakeem.myconotes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Genus;
import ch.joeakeem.myconotes.repository.GenusRepository;
import ch.joeakeem.myconotes.service.GenusService;
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
 * REST controller for managing {@link ch.joeakeem.myconotes.domain.Genus}.
 */
@RestController
@RequestMapping("/api")
public class GenusResource {

    private final Logger log = LoggerFactory.getLogger(GenusResource.class);

    private static final String ENTITY_NAME = "genus";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GenusService genusService;

    private final GenusRepository genusRepository;

    public GenusResource(GenusService genusService, GenusRepository genusRepository) {
        this.genusService = genusService;
        this.genusRepository = genusRepository;
    }

    /**
     * {@code POST  /genera} : Create a new genus.
     *
     * @param genus the genus to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new genus, or with status {@code 400 (Bad Request)} if the genus has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/genera")
    public ResponseEntity<Genus> createGenus(@Valid @RequestBody Genus genus) throws URISyntaxException {
        log.debug("REST request to save Genus : {}", genus);
        if (genus.getId() != null) {
            throw new BadRequestAlertException("A new genus cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Genus result = genusService.save(genus);
        return ResponseEntity
            .created(new URI("/api/genera/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /genera/:id} : Updates an existing genus.
     *
     * @param id the id of the genus to save.
     * @param genus the genus to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genus,
     * or with status {@code 400 (Bad Request)} if the genus is not valid,
     * or with status {@code 500 (Internal Server Error)} if the genus couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/genera/{id}")
    public ResponseEntity<Genus> updateGenus(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Genus genus)
        throws URISyntaxException {
        log.debug("REST request to update Genus : {}, {}", id, genus);
        if (genus.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, genus.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!genusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Genus result = genusService.update(genus);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, genus.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /genera/:id} : Partial updates given fields of an existing genus, field will ignore if it is null
     *
     * @param id the id of the genus to save.
     * @param genus the genus to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated genus,
     * or with status {@code 400 (Bad Request)} if the genus is not valid,
     * or with status {@code 404 (Not Found)} if the genus is not found,
     * or with status {@code 500 (Internal Server Error)} if the genus couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/genera/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Genus> partialUpdateGenus(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Genus genus
    ) throws URISyntaxException {
        log.debug("REST request to partial update Genus partially : {}, {}", id, genus);
        if (genus.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, genus.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!genusRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Genus> result = genusService.partialUpdate(genus);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, genus.getId().toString())
        );
    }

    /**
     * {@code GET  /genera} : get all the genera.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of genera in body.
     */
    @GetMapping("/genera")
    public ResponseEntity<List<Genus>> getAllGenera(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Genera");
        Page<Genus> page = genusService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /genera/:id} : get the "id" genus.
     *
     * @param id the id of the genus to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the genus, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/genera/{id}")
    public ResponseEntity<Genus> getGenus(@PathVariable Long id) {
        log.debug("REST request to get Genus : {}", id);
        Optional<Genus> genus = genusService.findOne(id);
        return ResponseUtil.wrapOrNotFound(genus);
    }

    /**
     * {@code DELETE  /genera/:id} : delete the "id" genus.
     *
     * @param id the id of the genus to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/genera/{id}")
    public ResponseEntity<Void> deleteGenus(@PathVariable Long id) {
        log.debug("REST request to delete Genus : {}", id);
        genusService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/genera?query=:query} : search for the genus corresponding
     * to the query.
     *
     * @param query the query of the genus search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/genera")
    public ResponseEntity<List<Genus>> searchGenera(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Genera for query {}", query);
        Page<Genus> page = genusService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
