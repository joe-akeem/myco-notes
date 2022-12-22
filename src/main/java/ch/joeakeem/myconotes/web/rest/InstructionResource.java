package ch.joeakeem.myconotes.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Instruction;
import ch.joeakeem.myconotes.repository.InstructionRepository;
import ch.joeakeem.myconotes.service.InstructionService;
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
 * REST controller for managing {@link ch.joeakeem.myconotes.domain.Instruction}.
 */
@RestController
@RequestMapping("/api")
public class InstructionResource {

    private final Logger log = LoggerFactory.getLogger(InstructionResource.class);

    private static final String ENTITY_NAME = "instruction";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final InstructionService instructionService;

    private final InstructionRepository instructionRepository;

    public InstructionResource(InstructionService instructionService, InstructionRepository instructionRepository) {
        this.instructionService = instructionService;
        this.instructionRepository = instructionRepository;
    }

    /**
     * {@code POST  /instructions} : Create a new instruction.
     *
     * @param instruction the instruction to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new instruction, or with status {@code 400 (Bad Request)} if the instruction has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/instructions")
    public ResponseEntity<Instruction> createInstruction(@Valid @RequestBody Instruction instruction) throws URISyntaxException {
        log.debug("REST request to save Instruction : {}", instruction);
        if (instruction.getId() != null) {
            throw new BadRequestAlertException("A new instruction cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Instruction result = instructionService.save(instruction);
        return ResponseEntity
            .created(new URI("/api/instructions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /instructions/:id} : Updates an existing instruction.
     *
     * @param id the id of the instruction to save.
     * @param instruction the instruction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instruction,
     * or with status {@code 400 (Bad Request)} if the instruction is not valid,
     * or with status {@code 500 (Internal Server Error)} if the instruction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/instructions/{id}")
    public ResponseEntity<Instruction> updateInstruction(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Instruction instruction
    ) throws URISyntaxException {
        log.debug("REST request to update Instruction : {}, {}", id, instruction);
        if (instruction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instruction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instructionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Instruction result = instructionService.update(instruction);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instruction.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /instructions/:id} : Partial updates given fields of an existing instruction, field will ignore if it is null
     *
     * @param id the id of the instruction to save.
     * @param instruction the instruction to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated instruction,
     * or with status {@code 400 (Bad Request)} if the instruction is not valid,
     * or with status {@code 404 (Not Found)} if the instruction is not found,
     * or with status {@code 500 (Internal Server Error)} if the instruction couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/instructions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Instruction> partialUpdateInstruction(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Instruction instruction
    ) throws URISyntaxException {
        log.debug("REST request to partial update Instruction partially : {}, {}", id, instruction);
        if (instruction.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, instruction.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!instructionRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Instruction> result = instructionService.partialUpdate(instruction);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, instruction.getId().toString())
        );
    }

    /**
     * {@code GET  /instructions} : get all the instructions.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of instructions in body.
     */
    @GetMapping("/instructions")
    public ResponseEntity<List<Instruction>> getAllInstructions(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false, defaultValue = "false") boolean eagerload
    ) {
        log.debug("REST request to get a page of Instructions");
        Page<Instruction> page;
        if (eagerload) {
            page = instructionService.findAllWithEagerRelationships(pageable);
        } else {
            page = instructionService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /instructions/:id} : get the "id" instruction.
     *
     * @param id the id of the instruction to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the instruction, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/instructions/{id}")
    public ResponseEntity<Instruction> getInstruction(@PathVariable Long id) {
        log.debug("REST request to get Instruction : {}", id);
        Optional<Instruction> instruction = instructionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(instruction);
    }

    /**
     * {@code DELETE  /instructions/:id} : delete the "id" instruction.
     *
     * @param id the id of the instruction to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/instructions/{id}")
    public ResponseEntity<Void> deleteInstruction(@PathVariable Long id) {
        log.debug("REST request to delete Instruction : {}", id);
        instructionService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/instructions?query=:query} : search for the instruction corresponding
     * to the query.
     *
     * @param query the query of the instruction search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/instructions")
    public ResponseEntity<List<Instruction>> searchInstructions(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of Instructions for query {}", query);
        Page<Instruction> page = instructionService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
