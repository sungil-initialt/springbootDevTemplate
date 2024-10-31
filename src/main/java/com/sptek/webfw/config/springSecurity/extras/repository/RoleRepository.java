package com.sptek.webfw.config.springSecurity.extras.repository;

import com.sptek.webfw.config.springSecurity.extras.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    default Optional<List<Role>> findAllAsOptional() {
        List<Role> roles = findAll();
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles);
    }

    Optional<List<Role>> findByRoleNameIn(List<String> roleNames);
}
