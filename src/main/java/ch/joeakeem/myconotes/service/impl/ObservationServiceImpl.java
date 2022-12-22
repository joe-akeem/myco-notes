package ch.joeakeem.myconotes.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Observation;
import ch.joeakeem.myconotes.repository.ObservationRepository;
import ch.joeakeem.myconotes.repository.search.ObservationSearchRepository;
import ch.joeakeem.myconotes.service.ObservationService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Observation}.
 */
@Service
@Transactional
public class ObservationServiceImpl implements ObservationService {

    private final Logger log = LoggerFactory.getLogger(ObservationServiceImpl.class);

    private final ObservationRepository observationRepository;

    private final ObservationSearchRepository observationSearchRepository;

    public ObservationServiceImpl(ObservationRepository observationRepository, ObservationSearchRepository observationSearchRepository) {
        this.observationRepository = observationRepository;
        this.observationSearchRepository = observationSearchRepository;
    }

    @Override
    public Observation save(Observation observation) {
        log.debug("Request to save Observation : {}", observation);
        Observation result = observationRepository.save(observation);
        observationSearchRepository.index(result);
        return result;
    }

    @Override
    public Observation update(Observation observation) {
        log.debug("Request to update Observation : {}", observation);
        Observation result = observationRepository.save(observation);
        observationSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Observation> partialUpdate(Observation observation) {
        log.debug("Request to partially update Observation : {}", observation);

        return observationRepository
            .findById(observation.getId())
            .map(existingObservation -> {
                if (observation.getObservationDate() != null) {
                    existingObservation.setObservationDate(observation.getObservationDate());
                }
                if (observation.getTitle() != null) {
                    existingObservation.setTitle(observation.getTitle());
                }
                if (observation.getDescription() != null) {
                    existingObservation.setDescription(observation.getDescription());
                }

                return existingObservation;
            })
            .map(observationRepository::save)
            .map(savedObservation -> {
                observationSearchRepository.save(savedObservation);

                return savedObservation;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Observation> findAll(Pageable pageable) {
        log.debug("Request to get all Observations");
        return observationRepository.findAll(pageable);
    }

    public Page<Observation> findAllWithEagerRelationships(Pageable pageable) {
        return observationRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Observation> findOne(Long id) {
        log.debug("Request to get Observation : {}", id);
        return observationRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Observation : {}", id);
        observationRepository.deleteById(id);
        observationSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Observation> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Observations for query {}", query);
        return observationSearchRepository.search(query, pageable);
    }
}
