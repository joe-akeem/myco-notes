package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Species;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Species entity.
 */
@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {
    default Optional<Species> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Species> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Species> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct species from Species species left join fetch species.genus",
        countQuery = "select count(distinct species) from Species species"
    )
    Page<Species> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct species from Species species left join fetch species.genus")
    List<Species> findAllWithToOneRelationships();

    @Query("select species from Species species left join fetch species.genus where species.id =:id")
    Optional<Species> findOneWithToOneRelationships(@Param("id") Long id);
}
