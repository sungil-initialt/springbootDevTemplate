package com.sptek._frameworkWebCore.springSecurity.extras.repository;

import com.sptek._frameworkWebCore.springSecurity.extras.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

}
