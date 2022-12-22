package ch.joeakeem.myconotes.service;

import ch.joeakeem.myconotes.domain.Experiment;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Experiment}.
 */
public interface ExperimentService {
    /**
     * Save a experiment.
     *
     * @param experiment the entity to save.
     * @return the persisted entity.
     */
    Experiment save(Experiment experiment);

    /**
     * Updates a experiment.
     *
     * @param experiment the entity to update.
     * @return the persisted entity.
     */
    Experiment update(Experiment experiment);

    /**
     * Partially updates a experiment.
     *
     * @param experiment the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Experiment> partialUpdate(Experiment experiment);

    /**
     * Get all the experiments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Experiment> findAll(Pageable pageable);

    /**
     * Get all the experiments with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Experiment> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" experiment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Experiment> findOne(Long id);

    /**
     * Delete the "id" experiment.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the experiment corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Experiment> search(String query, Pageable pageable);
}
