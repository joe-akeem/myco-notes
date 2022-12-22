package ch.joeakeem.myconotes.service;

import ch.joeakeem.myconotes.domain.Instruction;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Instruction}.
 */
public interface InstructionService {
    /**
     * Save a instruction.
     *
     * @param instruction the entity to save.
     * @return the persisted entity.
     */
    Instruction save(Instruction instruction);

    /**
     * Updates a instruction.
     *
     * @param instruction the entity to update.
     * @return the persisted entity.
     */
    Instruction update(Instruction instruction);

    /**
     * Partially updates a instruction.
     *
     * @param instruction the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Instruction> partialUpdate(Instruction instruction);

    /**
     * Get all the instructions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Instruction> findAll(Pageable pageable);

    /**
     * Get all the instructions with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Instruction> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" instruction.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Instruction> findOne(Long id);

    /**
     * Delete the "id" instruction.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the instruction corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Instruction> search(String query, Pageable pageable);
}
