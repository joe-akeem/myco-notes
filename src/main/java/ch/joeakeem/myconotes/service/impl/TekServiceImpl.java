package ch.joeakeem.myconotes.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Tek;
import ch.joeakeem.myconotes.repository.TekRepository;
import ch.joeakeem.myconotes.repository.search.TekSearchRepository;
import ch.joeakeem.myconotes.service.TekService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Tek}.
 */
@Service
@Transactional
public class TekServiceImpl implements TekService {

    private final Logger log = LoggerFactory.getLogger(TekServiceImpl.class);

    private final TekRepository tekRepository;

    private final TekSearchRepository tekSearchRepository;

    public TekServiceImpl(TekRepository tekRepository, TekSearchRepository tekSearchRepository) {
        this.tekRepository = tekRepository;
        this.tekSearchRepository = tekSearchRepository;
    }

    @Override
    public Tek save(Tek tek) {
        log.debug("Request to save Tek : {}", tek);
        Tek result = tekRepository.save(tek);
        tekSearchRepository.index(result);
        return result;
    }

    @Override
    public Tek update(Tek tek) {
        log.debug("Request to update Tek : {}", tek);
        Tek result = tekRepository.save(tek);
        tekSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Tek> partialUpdate(Tek tek) {
        log.debug("Request to partially update Tek : {}", tek);

        return tekRepository
            .findById(tek.getId())
            .map(existingTek -> {
                if (tek.getTitle() != null) {
                    existingTek.setTitle(tek.getTitle());
                }
                if (tek.getDescription() != null) {
                    existingTek.setDescription(tek.getDescription());
                }

                return existingTek;
            })
            .map(tekRepository::save)
            .map(savedTek -> {
                tekSearchRepository.save(savedTek);

                return savedTek;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Tek> findAll(Pageable pageable) {
        log.debug("Request to get all Teks");
        return tekRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tek> findOne(Long id) {
        log.debug("Request to get Tek : {}", id);
        return tekRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Tek : {}", id);
        tekRepository.deleteById(id);
        tekSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Tek> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Teks for query {}", query);
        return tekSearchRepository.search(query, pageable);
    }
}
