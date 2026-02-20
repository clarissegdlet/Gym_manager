package com.smartgym.manager.repository;

import com.smartgym.manager.domain.ClassSession;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClassSession entity.
 */
@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    default Optional<ClassSession> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<ClassSession> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<ClassSession> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select classSession from ClassSession classSession left join fetch classSession.coach left join fetch classSession.room",
        countQuery = "select count(classSession) from ClassSession classSession"
    )
    Page<ClassSession> findAllWithToOneRelationships(Pageable pageable);

    @Query("select classSession from ClassSession classSession left join fetch classSession.coach left join fetch classSession.room")
    List<ClassSession> findAllWithToOneRelationships();

    @Query(
        "select classSession from ClassSession classSession left join fetch classSession.coach left join fetch classSession.room where classSession.id =:id"
    )
    Optional<ClassSession> findOneWithToOneRelationships(@Param("id") Long id);
}
