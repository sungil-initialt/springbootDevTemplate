package com.sptek._frameworkWebCore.springSecurity.extras.repository;

import com.sptek._frameworkWebCore.springSecurity.extras.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

/*
    default Optional<List<Role>> findAllAsOptional() {
        //todo : 아래 둘중 뭐가 더 날까?
        return Optional.ofNullable(findAll());

        //List<Role> roles = findAll();
        //return roles.isEmpty() ? Optional.empty() : Optional.of(roles);
    }
*/

    List<Role> findByRoleNameIn(List<String> roleNames);
}
