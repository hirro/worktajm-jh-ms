package com.arnellconsulting.worktajm.ms.repository;

import com.arnellconsulting.worktajm.ms.domain.Organization;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Organization entity.
 */
@SuppressWarnings("unused")
public interface OrganizationRepository extends JpaRepository<Organization,Long> {

}
