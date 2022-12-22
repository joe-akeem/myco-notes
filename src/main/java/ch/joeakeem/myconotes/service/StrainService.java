package ch.joeakeem.myconotes.service;

import ch.joeakeem.myconotes.domain.Strain;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Strain}.
 */
public interface StrainService {
    /**
     * Save a strain.
     *
     * @param strain the entity to save.
     * @return the persisted entity.
     */
    Strain save(Strain strain);

    /**
     * Updates a strain.
     *
     * @param strain the entity to update.
     * @return the persisted entity.
     */
    Strain update(Strain strain);

    /**
     * Partially updates a strain.
     *
     * @param strain the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Strain> partialUpdate(Strain strain);

    /**
     * Get all the strains.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Strain> findAll(Pageable pageable);

    /**
     * Get all the strains with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Strain> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" strain.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Strain> findOne(Long id);

    /**
     * Delete the "id" strain.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the strain corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Strain> search(String query, Pageable pageable);
}
