package com.arnellconsulting.worktajm.ms.repository;

import com.arnellconsulting.worktajm.ms.domain.Project;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Project entity.
 */
@SuppressWarnings("unused")
public interface ProjectRepository extends JpaRepository<Project,Long> {

}
