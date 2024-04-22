package com.sptek.webfw.config.springSecurity.service;

import com.sptek.webfw.config.springSecurity.UserRole;
import com.sptek.webfw.util.ModelMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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

    public User getUserByEmail(String email) {
        User user = ModelMapperUtil.getObject(userRepository.findByEmail(email).orElse(new UserEntity()), User.class);

        //for execute time test.
        //User user2 = ModelMapperUtil.getObject(userRepository.findByEmail(email).orElse(new UserEntity()), User.class);
        //User user3 = ModelMapperUtil.getUser(userRepository.findByEmail(email).orElse(new UserEntity()));
        //User user4 = ModelMapperUtil.getUser(userRepository.findByEmail(email).orElse(new UserEntity()));

        return user;
    }
}
