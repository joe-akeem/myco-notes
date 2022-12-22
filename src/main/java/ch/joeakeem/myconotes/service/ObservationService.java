package ch.joeakeem.myconotes.service;

import ch.joeakeem.myconotes.domain.Observation;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Observation}.
 */
public interface ObservationService {
    /**
     * Save a observation.
     *
     * @param observation the entity to save.
     * @return the persisted entity.
     */
    Observation save(Observation observation);

    /**
     * Updates a observation.
     *
     * @param observation the entity to update.
     * @return the persisted entity.
     */
    Observation update(Observation observation);

    /**
     * Partially updates a observation.
     *
     * @param observation the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Observation> partialUpdate(Observation observation);

    /**
     * Get all the observations.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Observation> findAll(Pageable pageable);

    /**
     * Get all the observations with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Observation> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" observation.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Observation> findOne(Long id);

    /**
     * Delete the "id" observation.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the observation corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Observation> search(String query, Pageable pageable);
}
