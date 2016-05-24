package com.arnellconsulting.worktajm.ms.repository;

import com.arnellconsulting.worktajm.ms.domain.WorkLog;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the WorkLog entity.
 */
@SuppressWarnings("unused")
public interface WorkLogRepository extends JpaRepository<WorkLog,Long> {

}
