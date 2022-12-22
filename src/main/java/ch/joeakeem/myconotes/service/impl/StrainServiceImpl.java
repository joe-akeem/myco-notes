package ch.joeakeem.myconotes.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Strain;
import ch.joeakeem.myconotes.repository.StrainRepository;
import ch.joeakeem.myconotes.repository.search.StrainSearchRepository;
import ch.joeakeem.myconotes.service.StrainService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Strain}.
 */
@Service
@Transactional
public class StrainServiceImpl implements StrainService {

    private final Logger log = LoggerFactory.getLogger(StrainServiceImpl.class);

    private final StrainRepository strainRepository;

    private final StrainSearchRepository strainSearchRepository;

    public StrainServiceImpl(StrainRepository strainRepository, StrainSearchRepository strainSearchRepository) {
        this.strainRepository = strainRepository;
        this.strainSearchRepository = strainSearchRepository;
    }

    @Override
    public Strain save(Strain strain) {
        log.debug("Request to save Strain : {}", strain);
        Strain result = strainRepository.save(strain);
        strainSearchRepository.index(result);
        return result;
    }

    @Override
    public Strain update(Strain strain) {
        log.debug("Request to update Strain : {}", strain);
        Strain result = strainRepository.save(strain);
        strainSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Strain> partialUpdate(Strain strain) {
        log.debug("Request to partially update Strain : {}", strain);

        return strainRepository
            .findById(strain.getId())
            .map(existingStrain -> {
                if (strain.getName() != null) {
                    existingStrain.setName(strain.getName());
                }
                if (strain.getDescription() != null) {
                    existingStrain.setDescription(strain.getDescription());
                }
                if (strain.getIsolatedAt() != null) {
                    existingStrain.setIsolatedAt(strain.getIsolatedAt());
                }

                return existingStrain;
            })
            .map(strainRepository::save)
            .map(savedStrain -> {
                strainSearchRepository.save(savedStrain);

                return savedStrain;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Strain> findAll(Pageable pageable) {
        log.debug("Request to get all Strains");
        return strainRepository.findAll(pageable);
    }

    public Page<Strain> findAllWithEagerRelationships(Pageable pageable) {
        return strainRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Strain> findOne(Long id) {
        log.debug("Request to get Strain : {}", id);
        return strainRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Strain : {}", id);
        strainRepository.deleteById(id);
        strainSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Strain> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Strains for query {}", query);
        return strainSearchRepository.search(query, pageable);
    }
}
