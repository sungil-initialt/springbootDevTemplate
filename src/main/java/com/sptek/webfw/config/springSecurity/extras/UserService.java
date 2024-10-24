package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.config.springSecurity.RoleEnum;
import com.sptek.webfw.util.ModelMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TermsRepository termsRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserEntity saveUser(SignupRequestDto signupRequestDto){

        Set<RoleEntity> roleEntitySet = roleRepository.findByRoleEnumIn(signupRequestDto.getRoleNames())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.SERVICE_DEFAULT_ERROR, "no user role"));

        Set<TermsEntity> termsEntitySet = termsRepository.findByTermsNameIn(signupRequestDto.getTermsNames())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.SERVICE_DEFAULT_ERROR, "no user terms"));

        UserEntity userEntity = UserEntity.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .roleEntitySet(roleEntitySet)
                .termsEntitySet(termsEntitySet)
                .build();

        log.debug("new userEntity {}", userEntity);
        return userRepository.save(userEntity);
    }

    public List<String> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(roleEntity -> roleEntity.getRoleEnum().getValue())
                .collect(Collectors.toList());
    }

    public List<String> getAllTerms(){
        return termsRepository.findAll().stream()
                .map(termsEntity -> termsEntity.getTermsName())
                .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        //todo: 상세 처리 필요
        User user = null;
        return user;
    }
}
