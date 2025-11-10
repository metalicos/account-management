package com.komplikevych.AccountManagement.repository;

import com.komplikevych.AccountManagement.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}

