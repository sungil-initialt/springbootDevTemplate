package com.sptek._frameworkWebCore.config.springSecurity.extras.repository;

import com.sptek._frameworkWebCore.config.springSecurity.extras.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermsRepository extends JpaRepository<Terms, Long> {

    List<Terms> findByTermsNameIn(List<String> termsName);
}
