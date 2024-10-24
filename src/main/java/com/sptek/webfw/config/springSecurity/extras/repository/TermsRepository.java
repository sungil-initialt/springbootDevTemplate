package com.sptek.webfw.config.springSecurity.extras.repository;

import com.sptek.webfw.config.springSecurity.extras.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    Optional<Terms> findById(Long id);

    Optional<Set<Terms>> findByTermsNameIn(List<String> termsNames);

}
