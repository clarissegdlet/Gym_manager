package com.smartgym.manager.repository;

import com.smartgym.manager.domain.ClassSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ClassSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {}
