package ch.joeakeem.myconotes.service.impl;

import ch.joeakeem.myconotes.domain.Experiment;
import ch.joeakeem.myconotes.domain.Row;
import ch.joeakeem.myconotes.domain.RowBuilder;
import ch.joeakeem.myconotes.repository.ExperimentRepository;
import ch.joeakeem.myconotes.repository.search.ExperimentSearchRepository;
import ch.joeakeem.myconotes.service.ExperimentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Experiment}.
 */
@Service
@Transactional
public class ExperimentServiceImpl implements ExperimentService {

    private final Logger log = LoggerFactory.getLogger(ExperimentServiceImpl.class);

    private final ExperimentRepository experimentRepository;

    private final ExperimentSearchRepository experimentSearchRepository;

    public ExperimentServiceImpl(ExperimentRepository experimentRepository, ExperimentSearchRepository experimentSearchRepository) {
        this.experimentRepository = experimentRepository;
        this.experimentSearchRepository = experimentSearchRepository;
    }

    @Override
    public Experiment save(Experiment experiment) {
        log.debug("Request to save Experiment : {}", experiment);
        Experiment result = experimentRepository.save(experiment);
        experimentSearchRepository.index(result);
        return result;
    }

    @Override
    public Experiment update(Experiment experiment) {
        log.debug("Request to update Experiment : {}", experiment);
        Experiment result = experimentRepository.save(experiment);
        experimentSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Experiment> partialUpdate(Experiment experiment) {
        log.debug("Request to partially update Experiment : {}", experiment);

        return experimentRepository
            .findById(experiment.getId())
            .map(existingExperiment -> {
                if (experiment.getTitle() != null) {
                    existingExperiment.setTitle(experiment.getTitle());
                }
                if (experiment.getNotes() != null) {
                    existingExperiment.setNotes(experiment.getNotes());
                }
                if (experiment.getConductedAt() != null) {
                    existingExperiment.setConductedAt(experiment.getConductedAt());
                }

                return existingExperiment;
            })
            .map(experimentRepository::save)
            .map(savedExperiment -> {
                experimentSearchRepository.save(savedExperiment);

                return savedExperiment;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Experiment> findAll(Pageable pageable) {
        log.debug("Request to get all Experiments");
        return experimentRepository.findAll(pageable);
    }

    public Page<Experiment> findAllWithEagerRelationships(Pageable pageable) {
        return experimentRepository.findAllWithEagerRelationships(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Experiment> findOne(Long id) {
        log.debug("Request to get Experiment : {}", id);
        return experimentRepository.findOneWithEagerRelationships(id);
    }

    @Override
    public Optional<List<Row>> getChartData(Long id) {
        final Optional<Experiment> optionalExperiment = findOne(id);
        if (optionalExperiment.isEmpty()) {
            return Optional.empty();
        }
        final Experiment experiment = optionalExperiment.get();
        final RowBuilder rowBuilder = new RowBuilder();
        final List<Row> chartData = new ArrayList<>();
        addChartData(rowBuilder, chartData, experiment, null);
        return Optional.of(chartData);
    }

    private void addChartData(RowBuilder rowBuilder, List<Row> chartData, Experiment experiment, Experiment successor) {
        String dependencies = experiment.getPrecedingExperiments().stream().map(e -> e.getId().toString()).collect(Collectors.joining(","));

        chartData.add(
            rowBuilder
                .setTaskId(experiment.getId().toString())
                .setTaskName(experiment.getTitle())
                .setResource("Experiment")
                .setStartDate(experiment.getConductedAt())
                .setEndDate(successor != null ? successor.getConductedAt() : experiment.getConductedAt().plusDays(1))
                .setDependencies(dependencies)
                .createRow()
        );

        experiment
            .getPrecedingExperiments()
            .forEach(ex -> {
                addChartData(rowBuilder, chartData, ex, experiment);
            });
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Experiment : {}", id);
        experimentRepository.deleteById(id);
        experimentSearchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Experiment> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Experiments for query {}", query);
        return experimentSearchRepository.search(query, pageable);
    }
}
