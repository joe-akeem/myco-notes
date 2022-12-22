package ch.joeakeem.myconotes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Tek;
import ch.joeakeem.myconotes.repository.TekRepository;
import ch.joeakeem.myconotes.service.TekService;
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
 * REST controller for managing {@link ch.joeakeem.myconotes.domain.Tek}.
 */
@RestController
@RequestMapping("/api")
public class TekResource {

    private final Logger log = LoggerFactory.getLogger(TekResource.class);

    private static final String ENTITY_NAME = "tek";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TekService tekService;

    private final TekRepository tekRepository;

    public TekResource(TekService tekService, TekRepository tekRepository) {
        this.tekService = tekService;
        this.tekRepository = tekRepository;
    }

    /**
     * {@code POST  /teks} : Create a new tek.
     *
     * @param tek the tek to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tek, or with status {@code 400 (Bad Request)} if the tek has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/teks")
    public ResponseEntity<Tek> createTek(@Valid @RequestBody Tek tek) throws URISyntaxException {
        log.debug("REST request to save Tek : {}", tek);
        if (tek.getId() != null) {
            throw new BadRequestAlertException("A new tek cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Tek result = tekService.save(tek);
        return ResponseEntity
            .created(new URI("/api/teks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /teks/:id} : Updates an existing tek.
     *
     * @param id the id of the tek to save.
     * @param tek the tek to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tek,
     * or with status {@code 400 (Bad Request)} if the tek is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tek couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/teks/{id}")
    public ResponseEntity<Tek> updateTek(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Tek tek)
        throws URISyntaxException {
        log.debug("REST request to update Tek : {}, {}", id, tek);
        if (tek.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tek.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tekRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Tek result = tekService.update(tek);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tek.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /teks/:id} : Partial updates given fields of an existing tek, field will ignore if it is null
     *
     * @param id the id of the tek to save.
     * @param tek the tek to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tek,
     * or with status {@code 400 (Bad Request)} if the tek is not valid,
     * or with status {@code 404 (Not Found)} if the tek is not found,
     * or with status {@code 500 (Internal Server Error)} if the tek couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/teks/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Tek> partialUpdateTek(@PathVariable(value = "id", required = false) final Long id, @NotNull @RequestBody Tek tek)
        throws URISyntaxException {
        log.debug("REST request to partial update Tek partially : {}, {}", id, tek);
        if (tek.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tek.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tekRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Tek> result = tekService.partialUpdate(tek);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tek.getId().toString())
        );
    }

    /**
     * {@code GET  /teks} : get all the teks.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teks in body.
     */
    @GetMapping("/teks")
    public ResponseEntity<List<Tek>> getAllTeks(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of Teks");
        Page<Tek> page = tekService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /teks/:id} : get the "id" tek.
     *
     * @param id the id of the tek to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tek, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/teks/{id}")
    public ResponseEntity<Tek> getTek(@PathVariable Long id) {
        log.debug("REST request to get Tek : {}", id);
        Optional<Tek> tek = tekService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tek);
    }

    /**
     * {@code DELETE  /teks/:id} : delete the "id" tek.
     *
     * @param id the id of the tek to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/teks/{id}")
    public ResponseEntity<Void> deleteTek(@PathVariable Long id) {
        log.debug("REST request to delete Tek : {}", id);
        tekService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/teks?query=:query} : search for the tek corresponding
     * to the query.
     *
     * @param query the query of the tek search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/teks")
    public ResponseEntity<List<Tek>> searchTeks(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Teks for query {}", query);
        Page<Tek> page = tekService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
