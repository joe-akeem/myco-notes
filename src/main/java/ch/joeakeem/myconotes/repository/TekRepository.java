package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Tek;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Tek entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TekRepository extends JpaRepository<Tek, Long> {}
