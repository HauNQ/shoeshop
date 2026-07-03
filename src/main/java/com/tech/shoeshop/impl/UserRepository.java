package com.tech.shoeshop.impl;

import com.tech.shoeshop.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = "roles")
    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
