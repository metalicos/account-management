package com.komplikevych.AccountManagement.repository;

import com.komplikevych.AccountManagement.model.entity.UserRole;
import com.komplikevych.AccountManagement.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    
    List<UserRole> findByUser_Id(Long userId);
    
    Optional<UserRole> findByUser_IdAndRole(Long userId, Role role);
    
    void deleteByUser_Id(Long userId);
}

