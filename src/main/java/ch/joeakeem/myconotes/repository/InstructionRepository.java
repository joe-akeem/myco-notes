package ch.joeakeem.myconotes.repository;

import ch.joeakeem.myconotes.domain.Instruction;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Instruction entity.
 */
@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
    default Optional<Instruction> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Instruction> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Instruction> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select distinct instruction from Instruction instruction left join fetch instruction.instructionSet",
        countQuery = "select count(distinct instruction) from Instruction instruction"
    )
    Page<Instruction> findAllWithToOneRelationships(Pageable pageable);

    @Query("select distinct instruction from Instruction instruction left join fetch instruction.instructionSet")
    List<Instruction> findAllWithToOneRelationships();

    @Query("select instruction from Instruction instruction left join fetch instruction.instructionSet where instruction.id =:id")
    Optional<Instruction> findOneWithToOneRelationships(@Param("id") Long id);
}
