package ch.joeakeem.myconotes.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Genus;
import ch.joeakeem.myconotes.repository.GenusRepository;
import ch.joeakeem.myconotes.repository.search.GenusSearchRepository;
import ch.joeakeem.myconotes.service.GenusService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Genus}.
 */
@Service
@Transactional
public class GenusServiceImpl implements GenusService {

    private final Logger log = LoggerFactory.getLogger(GenusServiceImpl.class);

    private final GenusRepository genusRepository;

    private final GenusSearchRepository genusSearchRepository;

    public GenusServiceImpl(GenusRepository genusRepository, GenusSearchRepository genusSearchRepository) {
        this.genusRepository = genusRepository;
        this.genusSearchRepository = genusSearchRepository;
    }

    @Override
    public Genus save(Genus genus) {
        log.debug("Request to save Genus : {}", genus);
        Genus result = genusRepository.save(genus);
        genusSearchRepository.index(result);
        return result;
    }

    @Override
    public Genus update(Genus genus) {
        log.debug("Request to update Genus : {}", genus);
        Genus result = genusRepository.save(genus);
        genusSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Genus> partialUpdate(Genus genus) {
        log.debug("Request to partially update Genus : {}", genus);

        return genusRepository
            .findById(genus.getId())
            .map(existingGenus -> {
                if (genus.getName() != null) {
                    existingGenus.setName(genus.getName());
                }
                if (genus.getCommonName() != null) {
                    existingGenus.setCommonName(genus.getCommonName());
                }
                if (genus.getDescription() != null) {
                    existingGenus.setDescription(genus.getDescription());
                }

                return existingGenus;
            })
            .map(genusRepository::save)
            .map(savedGenus -> {
                genusSearchRepository.save(savedGenus);

                return savedGenus;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Genus> findAll(Pageable pageable) {
        log.debug("Request to get all Genera");
        return genusRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Genus> findOne(Long id) {
        log.debug("Request to get Genus : {}", id);
        return genusRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Genus : {}", id);
        genusRepository.deleteById(id);
        genusSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Genus> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Genera for query {}", query);
        return genusSearchRepository.search(query, pageable);
    }
}
