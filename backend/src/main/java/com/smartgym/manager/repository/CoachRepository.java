package com.smartgym.manager.repository;

import com.smartgym.manager.domain.Coach;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Coach entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {}
