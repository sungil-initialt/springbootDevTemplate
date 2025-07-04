package com.sptek._frameworkWebCore.springSecurity.extras.repository;

import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import com.sptek._frameworkWebCore.springSecurity.extras.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findByAuthorityIn(List<AuthorityEnum> authorities);
}
