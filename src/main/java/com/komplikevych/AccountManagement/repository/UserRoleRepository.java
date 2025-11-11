package com.komplikevych.AccountManagement.repository;

import com.komplikevych.AccountManagement.model.entity.UserRole;
import com.komplikevych.AccountManagement.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    void deleteUserRoleByUser_Id(Long userId);
}

