package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Observation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Observation entity.
 */
@Repository
public interface ObservationRepository extends JpaRepository<Observation, Long>, JpaSpecificationExecutor<Observation> {
    default Optional<Observation> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Observation> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Observation> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct observation from Observation observation left join fetch observation.experiment",
        countQuery = "select count(distinct observation) from Observation observation"
    )
    Page<Observation> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct observation from Observation observation left join fetch observation.experiment")
    List<Observation> findAllWithToOneRelationships();

    @Query("select observation from Observation observation left join fetch observation.experiment where observation.id =:id")
    Optional<Observation> findOneWithToOneRelationships(@Param("id") Long id);
}
