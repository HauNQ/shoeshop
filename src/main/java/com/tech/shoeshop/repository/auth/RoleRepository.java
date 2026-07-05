package com.tech.shoeshop.repository.auth;

import com.tech.shoeshop.entity.auth.Role;
import com.tech.shoeshop.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}
