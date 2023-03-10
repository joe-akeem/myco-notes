package ch.joeakeem.myconotes.service.impl;

import ch.joeakeem.myconotes.domain.Experiment;
import ch.joeakeem.myconotes.domain.Row;
import ch.joeakeem.myconotes.domain.Strain;
import ch.joeakeem.myconotes.repository.ExperimentRepository;
import ch.joeakeem.myconotes.repository.search.ExperimentSearchRepository;
import ch.joeakeem.myconotes.service.ExperimentService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

    public static final String EXPERIMENT_TO_EXPERIMENT_TOOLTIP =
        "<div style=\"margin=5px\"><ul>\n" +
        "  <li><a href=\"/experiment/%d/view\">%s (%s)</a></br></li>\n" +
        "  <li><a href=\"/experiment/%d/view\">%s (%s)</a></li>\n" +
        "  </ul>\n" +
        "</div>";

    public static final String EXPERIMENT_TO_STRAIN_TOOLTIP =
        "<div style=\"margin=5px\"><ul>\n" +
        "  <li><a href=\"/experiment/%d/view\">%s (%s)</a></br></li>\n" +
        "  <li><a href=\"/strain/%d/view\">%s (%s)</a></li>\n" +
        "  </ul>\n" +
        "</div>";

    public static final String STRAIN_TO_EXPERIMENT_TOOLTIP =
        "<div style=\"margin=5px\"><ul>\n" +
        "  <li><a href=\"/experiment/%d/view\">%s (%s)</a></br></li>\n" +
        "  <li><a href=\"/experiment/%d/view\">%s (%s)</a></li>\n" +
        "  </ul>\n" +
        "</div>";

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
        final List<Row> chartData = new ArrayList<>();
        addExperimentToExperimentChartData(chartData, experiment, experiment.getPrecedingExperiments());
        addStrainToExperimentChartData(chartData, experiment, experiment.getInvolvedStrains());
        return Optional.of(chartData);
    }

    private void addExperimentToExperimentChartData(List<Row> chartData, Experiment experiment, Set<Experiment> precedingExperiments) {
        precedingExperiments.forEach(preceding -> {
            log.debug("experiment ({}) -> experiment ({})", preceding.getId(), experiment.getId());
            chartData.add(new Row(title(preceding), title(experiment), 1, tooltip(preceding, experiment)));
            addExperimentToExperimentChartData(chartData, preceding, preceding.getPrecedingExperiments());
            addStrainToExperimentChartData(chartData, preceding, preceding.getInvolvedStrains());
        });
    }

    private void addStrainToExperimentChartData(List<Row> chartData, Experiment experiment, Set<Strain> involvedStrains) {
        involvedStrains.forEach(strain -> {
            log.debug("strain ({}) -> experiment ({})", strain.getId(), experiment.getId());
            chartData.add(new Row(title(strain), title(experiment), 1, tooltip(strain, experiment)));
            addExperimentToStrainChartData(chartData, strain, strain.getOrigin());
        });
    }

    private void addExperimentToStrainChartData(List<Row> chartData, Strain strain, Experiment origin) {
        chartData.add(new Row(title(origin), title(strain), 1, tooltip(origin, strain)));
        addExperimentToExperimentChartData(chartData, origin, origin.getPrecedingExperiments());
        addStrainToExperimentChartData(chartData, origin, origin.getInvolvedStrains());
    }

    private String tooltip(Experiment from, Experiment to) {
        return String.format(
            EXPERIMENT_TO_EXPERIMENT_TOOLTIP,
            from.getId(),
            from.getTitle(),
            from.getConductedAt(),
            to.getId(),
            to.getTitle(),
            to.getConductedAt()
        );
    }

    private String tooltip(Experiment from, Strain to) {
        return String.format(
            EXPERIMENT_TO_STRAIN_TOOLTIP,
            from.getId(),
            from.getTitle(),
            from.getConductedAt(),
            to.getId(),
            to.getName(),
            to.getIsolatedAt()
        );
    }

    private String tooltip(Strain from, Experiment to) {
        return String.format(
            STRAIN_TO_EXPERIMENT_TOOLTIP,
            from.getId(),
            from.getName(),
            from.getIsolatedAt(),
            to.getId(),
            to.getTitle(),
            to.getConductedAt()
        );
    }

    private String title(Experiment experiment) {
        return experiment.getTitle() + "(" + experiment.getConductedAt() + ")";
    }

    private String title(Strain strain) {
        return strain.getName() + "(" + strain.getIsolatedAt() + ")";
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
