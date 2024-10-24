package com.sptek.webfw.config.springSecurity.extras;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TermsRepository extends JpaRepository<TermsEntity, Long> {
    Optional<TermsEntity> findById(Long id);

    Optional<Set<TermsEntity>> findByTermsNameIn(List<String> termsNames);

}
