package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.config.springSecurity.extras.dto.*;
import com.sptek.webfw.config.springSecurity.extras.entity.*;
import com.sptek.webfw.config.springSecurity.extras.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SecurityService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TermsRepository termsRepository;
    private final TestRepository testRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(SignupRequestDto signupRequestDto){
        List<RoleDto> roles = findRolesByRoleNameIn(signupRequestDto.getRoles().stream().map(RoleDto::getRoleName).collect(Collectors.toList()));
        List<TermsDto> terms = findTermsByTermsNameIn(signupRequestDto.getTerms().stream().map(TermsDto::getTermsName).collect(Collectors.toList()));

        signupRequestDto.setRoles(roles);
        signupRequestDto.setTerms(terms);
        signupRequestDto.setPassword(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()));
        User user = modelMapper.map(signupRequestDto, User.class);
        log.debug("new userEntity : {}", user);
        return userRepository.save(user);
    }

    public List<RoleDto> findAllRoles(){
        List<Role> roles = Optional.of(roleRepository.findAll())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Role found"));

        return modelMapper.map(roles, new TypeToken<List<RoleDto>>() {}.getType());

//        return Optional.ofNullable(roleRepository.findAll())
//                .filter(roles -> !roles.isEmpty())
//                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Role found"))
//                .stream()
//                .map(role -> modelMapper.map(role, RoleDto.class))
//                .collect(Collectors.toList());
    }

    public List<RoleDto> findRolesByRoleNameIn(List<String> roleNames){
        List<Role> roles = Optional.of(roleRepository.findByRoleNameIn(roleNames))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Role found"));

        return modelMapper.map(roles, new TypeToken<List<RoleDto>>() {}.getType());
    }

    public List<TermsDto> findAllTerms(){
        List<Terms> terms = Optional.of(termsRepository.findAll())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Terms found"));

        return modelMapper.map(terms, new TypeToken<List<TermsDto>>() {}.getType());
    }

    public List<TermsDto> findTermsByTermsNameIn(List<String> termsNames){
        List<Terms> terms = Optional.of(termsRepository.findByTermsNameIn(termsNames))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Terms found"));

        return modelMapper.map(terms, new TypeToken<List<TermsDto>>() {}.getType());
    }

    public UserDto findUserByEmail(String email) {
        return modelMapper.map(
                userRepository.findByEmail(email).orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, String.format("No user found with this email : %s", email)))
                , UserDto.class);
    }

    public List<AuthorityDto> findAllAuthorities() {
        List<Authority> authorities = Optional.of(authorityRepository.findAll())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Authority found"));

        return modelMapper.map(authorities, new TypeToken<List<AuthorityDto>>() {}.getType());
    }

    public List<RoleDto> saveRoles(RoleMngRequestDto roleMngRequestDto){
        Map<Long, RoleDto> reqRolesMap = roleMngRequestDto.getAllRoles().stream().collect(Collectors.toMap(RoleDto::getId, role -> role));
        List<Role> orgRoles = roleRepository.findAllById(reqRolesMap.keySet());

        for(Role orgRole : orgRoles){
            orgRole.setRoleName(reqRolesMap.get(orgRole.getId()).getRoleName());

            Optional.ofNullable(reqRolesMap.get(orgRole.getId()).getAuthorities())
                    .ifPresentOrElse(
                            authorities -> orgRole.setAuthorities(
                                    authorityRepository.findByAuthorityIn(
                                            authorities.stream()
                                                    .map(AuthorityDto::getAuthority)
                                                    .toList()
                                    )
                            ),
                            () -> orgRole.setAuthorities(Collections.emptyList())
                    );
        }

        return modelMapper.map(roleRepository.saveAll(orgRoles), new TypeToken<List<RoleDto>>() {}.getType());
    }
}
