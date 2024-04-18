package com.sptek.webfw.config.springSecurity.service;

import com.sptek.webfw.config.springSecurity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
