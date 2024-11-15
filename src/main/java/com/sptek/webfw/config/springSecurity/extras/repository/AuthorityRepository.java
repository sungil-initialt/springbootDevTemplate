package com.sptek.webfw.config.springSecurity.extras.repository;

import com.sptek.webfw.config.springSecurity.AuthorityIfEnum;
import com.sptek.webfw.config.springSecurity.extras.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findByAuthorityIn(List<AuthorityIfEnum> authorities);
}
