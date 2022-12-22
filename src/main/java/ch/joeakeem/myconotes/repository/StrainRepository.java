package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Strain;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Strain entity.
 */
@Repository
public interface StrainRepository extends JpaRepository<Strain, Long> {
    default Optional<Strain> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Strain> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Strain> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct strain from Strain strain left join fetch strain.species left join fetch strain.origin",
        countQuery = "select count(distinct strain) from Strain strain"
    )
    Page<Strain> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct strain from Strain strain left join fetch strain.species left join fetch strain.origin")
    List<Strain> findAllWithToOneRelationships();

    @Query("select strain from Strain strain left join fetch strain.species left join fetch strain.origin where strain.id =:id")
    Optional<Strain> findOneWithToOneRelationships(@Param("id") Long id);
}
