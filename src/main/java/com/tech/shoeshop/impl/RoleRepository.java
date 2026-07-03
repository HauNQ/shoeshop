package com.tech.shoeshop.impl;

import com.tech.shoeshop.entity.Role;
import com.tech.shoeshop.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
