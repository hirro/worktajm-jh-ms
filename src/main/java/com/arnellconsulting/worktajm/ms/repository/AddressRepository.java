package com.arnellconsulting.worktajm.ms.repository;

import com.arnellconsulting.worktajm.ms.domain.Address;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Address entity.
 */
@SuppressWarnings("unused")
public interface AddressRepository extends JpaRepository<Address,Long> {

}
