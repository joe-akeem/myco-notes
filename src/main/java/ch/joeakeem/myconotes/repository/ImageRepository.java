package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Image;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Image entity.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    default Optional<Image> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Image> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Image> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct image from Image image left join fetch image.observation left join fetch image.strain left join fetch image.tek",
        countQuery = "select count(distinct image) from Image image"
    )
    Page<Image> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select distinct image from Image image left join fetch image.observation left join fetch image.strain left join fetch image.tek"
    )
    List<Image> findAllWithToOneRelationships();

    @Query(
        "select image from Image image left join fetch image.observation left join fetch image.strain left join fetch image.tek where image.id =:id"
    )
    Optional<Image> findOneWithToOneRelationships(@Param("id") Long id);
}
