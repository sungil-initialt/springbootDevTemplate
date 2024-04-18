package com.sptek.webfw.config.springSecurity.service;

import com.sptek.webfw.config.springSecurity.User;
import com.sptek.webfw.config.springSecurity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(SignupRequestDto signupRequestDto){
        return userRepository.save(
                User.builder()
                        .email(signupRequestDto.getEmail())
                        .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassWord()))
                        .userRole(UserRole.getUserRoleFromValue(signupRequestDto.getUserRole()))
                        .build()
        );
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
