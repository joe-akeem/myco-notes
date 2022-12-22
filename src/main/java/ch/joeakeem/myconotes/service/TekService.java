package ch.joeakeem.myconotes.service;

import ch.joeakeem.myconotes.domain.Tek;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Tek}.
 */
public interface TekService {
    /**
     * Save a tek.
     *
     * @param tek the entity to save.
     * @return the persisted entity.
     */
    Tek save(Tek tek);

    /**
     * Updates a tek.
     *
     * @param tek the entity to update.
     * @return the persisted entity.
     */
    Tek update(Tek tek);

    /**
     * Partially updates a tek.
     *
     * @param tek the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Tek> partialUpdate(Tek tek);

    /**
     * Get all the teks.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Tek> findAll(Pageable pageable);

    /**
     * Get the "id" tek.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Tek> findOne(Long id);

    /**
     * Delete the "id" tek.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the tek corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Tek> search(String query, Pageable pageable);
}
