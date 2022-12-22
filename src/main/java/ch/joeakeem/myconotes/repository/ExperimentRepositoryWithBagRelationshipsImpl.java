package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Experiment;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.annotations.QueryHints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class ExperimentRepositoryWithBagRelationshipsImpl implements ExperimentRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Experiment> fetchBagRelationships(Optional<Experiment> experiment) {
        return experiment.map(this::fetchInvolvedStrains).map(this::fetchPrecedingExperiments);
    }

    @Override
    public Page<Experiment> fetchBagRelationships(Page<Experiment> experiments) {
        return new PageImpl<>(fetchBagRelationships(experiments.getContent()), experiments.getPageable(), experiments.getTotalElements());
    }

    @Override
    public List<Experiment> fetchBagRelationships(List<Experiment> experiments) {
        return Optional
            .of(experiments)
            .map(this::fetchInvolvedStrains)
            .map(this::fetchPrecedingExperiments)
            .orElse(Collections.emptyList());
    }

    Experiment fetchInvolvedStrains(Experiment result) {
        return entityManager
            .createQuery(
                "select experiment from Experiment experiment left join fetch experiment.involvedStrains where experiment is :experiment",
                Experiment.class
            )
            .setParameter("experiment", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Experiment> fetchInvolvedStrains(List<Experiment> experiments) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, experiments.size()).forEach(index -> order.put(experiments.get(index).getId(), index));
        List<Experiment> result = entityManager
            .createQuery(
                "select distinct experiment from Experiment experiment left join fetch experiment.involvedStrains where experiment in :experiments",
                Experiment.class
            )
            .setParameter("experiments", experiments)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }

    Experiment fetchPrecedingExperiments(Experiment result) {
        return entityManager
            .createQuery(
                "select experiment from Experiment experiment left join fetch experiment.precedingExperiments where experiment is :experiment",
                Experiment.class
            )
            .setParameter("experiment", result)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getSingleResult();
    }

    List<Experiment> fetchPrecedingExperiments(List<Experiment> experiments) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, experiments.size()).forEach(index -> order.put(experiments.get(index).getId(), index));
        List<Experiment> result = entityManager
            .createQuery(
                "select distinct experiment from Experiment experiment left join fetch experiment.precedingExperiments where experiment in :experiments",
                Experiment.class
            )
            .setParameter("experiments", experiments)
            .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
