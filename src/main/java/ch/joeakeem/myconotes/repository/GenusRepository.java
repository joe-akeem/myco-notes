package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Genus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Genus entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GenusRepository extends JpaRepository<Genus, Long> {}
