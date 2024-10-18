package com.sptek.webfw.config.springSecurity.extras;

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
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                //.userRoleEntitySet((signupRequestDto.getUserRoleEntitySet()))
                .build();


        log.debug("new userEntity {}", userEntity);
        userRepository.save(userEntity);

        return ModelMapperUtil.map(userEntity, User.class);
    }

    public User getUserByEmail(String email) {
        //todo: 상세 처리 필요
        User user = null;
        return user;
    }
}
