package com.komplikevych.AccountManagement.repository;

import com.komplikevych.AccountManagement.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u")
    Page<User> findAllActive(Pageable pageable);
}

