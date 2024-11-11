package com.sptek.webfw.config.springSecurity.extras.repository;

import com.sptek.webfw.config.springSecurity.AuthorityEnum;
import com.sptek.webfw.config.springSecurity.extras.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    default Optional<List< Authority>> findAllAsOptional() {
        List< Authority>  authorities = findAll();
        return authorities.isEmpty() ? Optional.empty() : Optional.of(authorities);
    }

    List<Authority> findByAuthorityIn(List<AuthorityEnum> authorities);
}
