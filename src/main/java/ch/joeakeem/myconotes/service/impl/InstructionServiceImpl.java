package ch.joeakeem.myconotes.service.impl;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.joeakeem.myconotes.domain.Instruction;
import ch.joeakeem.myconotes.repository.InstructionRepository;
import ch.joeakeem.myconotes.repository.search.InstructionSearchRepository;
import ch.joeakeem.myconotes.service.InstructionService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Instruction}.
 */
@Service
@Transactional
public class InstructionServiceImpl implements InstructionService {

    private final Logger log = LoggerFactory.getLogger(InstructionServiceImpl.class);

    private final InstructionRepository instructionRepository;

    private final InstructionSearchRepository instructionSearchRepository;

    public InstructionServiceImpl(InstructionRepository instructionRepository, InstructionSearchRepository instructionSearchRepository) {
        this.instructionRepository = instructionRepository;
        this.instructionSearchRepository = instructionSearchRepository;
    }

    @Override
    public Instruction save(Instruction instruction) {
        log.debug("Request to save Instruction : {}", instruction);
        Instruction result = instructionRepository.save(instruction);
        instructionSearchRepository.index(result);
        return result;
    }

    @Override
    public Instruction update(Instruction instruction) {
        log.debug("Request to update Instruction : {}", instruction);
        Instruction result = instructionRepository.save(instruction);
        instructionSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Instruction> partialUpdate(Instruction instruction) {
        log.debug("Request to partially update Instruction : {}", instruction);

        return instructionRepository
            .findById(instruction.getId())
            .map(existingInstruction -> {
                if (instruction.getTitle() != null) {
                    existingInstruction.setTitle(instruction.getTitle());
                }
                if (instruction.getDescription() != null) {
                    existingInstruction.setDescription(instruction.getDescription());
                }

                return existingInstruction;
            })
            .map(instructionRepository::save)
            .map(savedInstruction -> {
                instructionSearchRepository.save(savedInstruction);

                return savedInstruction;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Instruction> findAll(Pageable pageable) {
        log.debug("Request to get all Instructions");
        return instructionRepository.findAll(pageable);
    }

    public Page<Instruction> findAllWithEagerRelationships(Pageable pageable) {
        return instructionRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instruction> findOne(Long id) {
        log.debug("Request to get Instruction : {}", id);
        return instructionRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Instruction : {}", id);
        instructionRepository.deleteById(id);
        instructionSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Instruction> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Instructions for query {}", query);
        return instructionSearchRepository.search(query, pageable);
    }
}
