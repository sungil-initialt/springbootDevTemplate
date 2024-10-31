package com.sptek.webfw.config.springSecurity.extras.repository;

import com.sptek.webfw.config.springSecurity.extras.entity.Authority;
import com.sptek.webfw.config.springSecurity.extras.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
