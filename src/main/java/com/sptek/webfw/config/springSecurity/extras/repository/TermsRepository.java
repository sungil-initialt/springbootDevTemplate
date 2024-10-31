package com.sptek.webfw.config.springSecurity.extras.repository;

import com.sptek.webfw.config.springSecurity.extras.entity.Role;
import com.sptek.webfw.config.springSecurity.extras.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TermsRepository extends JpaRepository<Terms, Long> {
    default Optional<List<Terms>> findAllAsOptional() {
        List<Terms> terms = findAll();
        return terms.isEmpty() ? Optional.empty() : Optional.of(terms);
    }

    Optional<List<Terms>> findByTermsNameIn(List<String> termsName);
}
