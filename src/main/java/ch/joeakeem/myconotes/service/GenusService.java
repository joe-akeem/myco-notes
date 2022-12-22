package ch.joeakeem.myconotes.service;

import ch.joeakeem.myconotes.domain.Genus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Genus}.
 */
public interface GenusService {
    /**
     * Save a genus.
     *
     * @param genus the entity to save.
     * @return the persisted entity.
     */
    Genus save(Genus genus);

    /**
     * Updates a genus.
     *
     * @param genus the entity to update.
     * @return the persisted entity.
     */
    Genus update(Genus genus);

    /**
     * Partially updates a genus.
     *
     * @param genus the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Genus> partialUpdate(Genus genus);

    /**
     * Get all the genera.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Genus> findAll(Pageable pageable);

    /**
     * Get the "id" genus.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Genus> findOne(Long id);

    /**
     * Delete the "id" genus.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the genus corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Genus> search(String query, Pageable pageable);
}
