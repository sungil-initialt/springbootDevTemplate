package com.sptek.webfw.config.springSecurity.extras.repository;

import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.config.springSecurity.extras.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

}
