package ch.joeakeem.myconotes.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Species;
import ch.joeakeem.myconotes.repository.SpeciesRepository;
import ch.joeakeem.myconotes.repository.search.SpeciesSearchRepository;
import ch.joeakeem.myconotes.service.SpeciesService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Species}.
 */
@Service
@Transactional
public class SpeciesServiceImpl implements SpeciesService {

    private final Logger log = LoggerFactory.getLogger(SpeciesServiceImpl.class);

    private final SpeciesRepository speciesRepository;

    private final SpeciesSearchRepository speciesSearchRepository;

    public SpeciesServiceImpl(SpeciesRepository speciesRepository, SpeciesSearchRepository speciesSearchRepository) {
        this.speciesRepository = speciesRepository;
        this.speciesSearchRepository = speciesSearchRepository;
    }

    @Override
    public Species save(Species species) {
        log.debug("Request to save Species : {}", species);
        Species result = speciesRepository.save(species);
        speciesSearchRepository.index(result);
        return result;
    }

    @Override
    public Species update(Species species) {
        log.debug("Request to update Species : {}", species);
        Species result = speciesRepository.save(species);
        speciesSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Species> partialUpdate(Species species) {
        log.debug("Request to partially update Species : {}", species);

        return speciesRepository
            .findById(species.getId())
            .map(existingSpecies -> {
                if (species.getName() != null) {
                    existingSpecies.setName(species.getName());
                }
                if (species.getCommonName() != null) {
                    existingSpecies.setCommonName(species.getCommonName());
                }
                if (species.getDescription() != null) {
                    existingSpecies.setDescription(species.getDescription());
                }

                return existingSpecies;
            })
            .map(speciesRepository::save)
            .map(savedSpecies -> {
                speciesSearchRepository.save(savedSpecies);

                return savedSpecies;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Species> findAll(Pageable pageable) {
        log.debug("Request to get all Species");
        return speciesRepository.findAll(pageable);
    }

    public Page<Species> findAllWithEagerRelationships(Pageable pageable) {
        return speciesRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Species> findOne(Long id) {
        log.debug("Request to get Species : {}", id);
        return speciesRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Species : {}", id);
        speciesRepository.deleteById(id);
        speciesSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Species> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Species for query {}", query);
        return speciesSearchRepository.search(query, pageable);
    }
}
