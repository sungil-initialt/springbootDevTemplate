package com.sptek.webfw.config.springSecurity.support;

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
        UserEntity userEntity = UserEntity.builder()
                .email(signupRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .userRole(UserRole.getUserRoleFromValue(signupRequestDto.getUserRole()))
                .build();


        log.debug("new userEntity {}", userEntity);
        userRepository.save(userEntity);

        return ModelMapperUtil.map(userEntity, User.class);
    }

    public User getUserByEmail(String email) {
        User user = ModelMapperUtil.map(userRepository.findByEmail(email).orElse(new UserEntity()), User.class);
        return user;
    }
}
