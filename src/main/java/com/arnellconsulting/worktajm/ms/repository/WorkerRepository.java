package com.arnellconsulting.worktajm.ms.repository;

import com.arnellconsulting.worktajm.ms.domain.Worker;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Worker entity.
 */
@SuppressWarnings("unused")
public interface WorkerRepository extends JpaRepository<Worker,Long> {

}
