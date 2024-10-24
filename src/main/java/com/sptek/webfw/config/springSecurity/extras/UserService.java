package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.common.code.BaseCode;
import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.config.springSecurity.extras.dto.SignupRequestDto;
import com.sptek.webfw.config.springSecurity.extras.dto.UserDto;
import com.sptek.webfw.config.springSecurity.extras.entity.Role;
import com.sptek.webfw.config.springSecurity.extras.entity.Terms;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.config.springSecurity.extras.repository.RoleRepository;
import com.sptek.webfw.config.springSecurity.extras.repository.TermsRepository;
import com.sptek.webfw.config.springSecurity.extras.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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
    private ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TermsRepository termsRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(SignupRequestDto signupRequestDto){

        Set<Role> roleSet = roleRepository.findByRoleEnumIn(signupRequestDto.getRoleNames())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.SERVICE_DEFAULT_ERROR, "no user role"));

        Set<Terms> termsSet = termsRepository.findByTermsNameIn(signupRequestDto.getTermsNames())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.SERVICE_DEFAULT_ERROR, "no user terms"));

        User user = User.builder()
                .name(signupRequestDto.getName())
                .email(signupRequestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .roleSet(roleSet)
                .termsSet(termsSet)
                .build();

        log.debug("new userEntity {}", user);
        return userRepository.save(user);
    }

    public List<String> getAllRoles(){
        return roleRepository.findAll().stream()
                .map(role -> role.getRoleEnum().getValue())
                .collect(Collectors.toList());
    }

    public List<String> getAllTerms(){
        return termsRepository.findAll().stream()
                .map(terms -> terms.getTermsName())
                .collect(Collectors.toList());
    }

    public UserDto getUserByEmail(String email) {
        return modelMapper.map(
                    userRepository.findByEmail(email).orElseThrow(() ->
                            new ServiceException(ServiceErrorCodeEnum.SERVICE_DEFAULT_ERROR, String.format("No user found with this email : %s", email)))
                , UserDto.class);
    }
}
