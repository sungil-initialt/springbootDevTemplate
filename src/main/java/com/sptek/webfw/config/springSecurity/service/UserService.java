package com.sptek.webfw.config.springSecurity.service;

import com.sptek.webfw.config.springSecurity.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(SignupRequestDto signupRequestDto){
        UserEntity newUser = UserEntity.builder()
                .email(signupRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .userRole(UserRole.getUserRoleFromValue(signupRequestDto.getUserRole()))
                .build();

        log.debug("new User {}", newUser);
        userRepository.save(newUser);
        return User.builder()
                .email(newUser.getEmail())
                .password(newUser.getPassword())
                .userRole(newUser.getUserRole())
                .build();
    }

    public Optional<UserEntity> getUserByEmail(String email) {
        return  userRepository.findByEmail(email);
        //----> 여기부터 수정해야 함.
        //UserEntity userEntity = userRepository.findByEmail(email).ifPresent(userEntity);
    }
}
