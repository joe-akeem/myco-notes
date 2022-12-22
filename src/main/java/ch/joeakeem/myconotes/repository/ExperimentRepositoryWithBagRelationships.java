package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Experiment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface ExperimentRepositoryWithBagRelationships {
    Optional<Experiment> fetchBagRelationships(Optional<Experiment> experiment);

    List<Experiment> fetchBagRelationships(List<Experiment> experiments);

    Page<Experiment> fetchBagRelationships(Page<Experiment> experiments);
}
