package com.sptek._frameworkWebCore.springSecurity.extras.repository;

import com.sptek._frameworkWebCore.springSecurity.extras.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}