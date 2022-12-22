package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Experiment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Experiment entity.
 *
 * When extending this class, extend ExperimentRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface ExperimentRepository extends ExperimentRepositoryWithBagRelationships, JpaRepository<Experiment, Long> {
    @Query("select experiment from Experiment experiment where experiment.conductedBy.login = ?#{principal.username}")
    List<Experiment> findByConductedByIsCurrentUser();

    default Optional<Experiment> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<Experiment> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<Experiment> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select distinct experiment from Experiment experiment left join fetch experiment.instructions left join fetch experiment.conductedBy",
        countQuery = "select count(distinct experiment) from Experiment experiment"
    )
    Page<Experiment> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct experiment from Experiment experiment left join fetch experiment.instructions left join fetch experiment.conductedBy"
    )
    List<Experiment> findAllWithToOneRelationships();

    @Query(
        "select experiment from Experiment experiment left join fetch experiment.instructions left join fetch experiment.conductedBy where experiment.id =:id"
    )
    Optional<Experiment> findOneWithToOneRelationships(@Param("id") Long id);
}
