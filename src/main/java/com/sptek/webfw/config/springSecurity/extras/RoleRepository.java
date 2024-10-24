package com.sptek.webfw.config.springSecurity.extras;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findById(Long id);

    Optional<Set<RoleEntity>> findByRoleEnumIn(List<String> roleNames);
}
