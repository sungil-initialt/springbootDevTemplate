package com.sptek._frameworkWebCore._example.unit.authentication;

import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.*;
import com.sptek._frameworkWebCore.springSecurity.extras.entity.*;
import com.sptek._frameworkWebCore.springSecurity.extras.repository.AuthorityRepository;
import com.sptek._frameworkWebCore.springSecurity.extras.repository.RoleRepository;
import com.sptek._frameworkWebCore.springSecurity.extras.repository.TermsRepository;
import com.sptek._frameworkWebCore.springSecurity.extras.repository.UserRepository;
import com.sptek._projectCommon.commonObject.code.ServiceErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service

/*
JPA 사용과 관련해서
readonly=true 에서 가져온 엔티티 를 readonly=false 에서 수정 해도 더티 체킹 되어 db에 반영됨
그러나 readonly 로 가져온 엔티티 가 업데이트에 관여 하는 구조가 코드상 모호해 보일수 있음.
해결로.. 별도의 findUserForUpdate 이런 식의 중복?되지만 별도 find 를 만들 어서 readonly=false 로 해서 쓰던가..
그런면에서 서비스는 return 은 entity 가 아닌 entityDto 형태로 전달해 주는 구조가 젤 좋을 듯하다... (번거러운 코드가 생기겠지만..)
*/

public class AuthenticationService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TermsRepository termsRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
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

    @Transactional
    public User updateUser(UserUpdateRequestDto userUpdateRequestDto){
        List<RoleDto> roles = findRolesByRoleNameIn(userUpdateRequestDto.getRoles().stream().map(RoleDto::getRoleName).collect(Collectors.toList()));
        List<TermsDto> terms = findTermsByTermsNameIn(userUpdateRequestDto.getTerms().stream().map(TermsDto::getTermsName).collect(Collectors.toList()));

        userUpdateRequestDto.setRoles(roles);
        userUpdateRequestDto.setTerms(terms);
        userUpdateRequestDto.setPassword(bCryptPasswordEncoder.encode(userUpdateRequestDto.getPassword()));

        User originUser = userRepository.findByEmail(userUpdateRequestDto.getEmail())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, String.format("No user found with this email : %s", userUpdateRequestDto.getEmail())));

        originUser.setName(userUpdateRequestDto.getName());
        originUser.setEmail(userUpdateRequestDto.getEmail());
        originUser.setPassword(userUpdateRequestDto.getPassword());
        originUser.setUserAddresses(modelMapper.map(userUpdateRequestDto.getUserAddresses(), new TypeToken<List<UserAddress>>() {}.getType()));
        originUser.setRoles(modelMapper.map(userUpdateRequestDto.getRoles(), new TypeToken<List<Role>>() {}.getType()));
        originUser.setTerms(modelMapper.map(userUpdateRequestDto.getTerms(), new TypeToken<List<Terms>>() {}.getType()));


        log.debug("update userEntity : {}", originUser);
        return originUser;
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<RoleDto> findRolesByRoleNameIn(List<String> roleNames){
        List<Role> roles = Optional.of(roleRepository.findByRoleNameIn(roleNames))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Role found"));

        return modelMapper.map(roles, new TypeToken<List<RoleDto>>() {}.getType());
    }

    @Transactional(readOnly = true)
    public List<TermsDto> findAllTerms(){
        List<Terms> terms = Optional.of(termsRepository.findAll())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Terms found"));

        return modelMapper.map(terms, new TypeToken<List<TermsDto>>() {}.getType());
    }

    @Transactional(readOnly = true)
    public List<TermsDto> findTermsByTermsNameIn(List<String> termsNames){
        List<Terms> terms = Optional.of(termsRepository.findByTermsNameIn(termsNames))
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Terms found"));

        return modelMapper.map(terms, new TypeToken<List<TermsDto>>() {}.getType());
    }

    @Transactional(readOnly = true)
    public UserDto findUserByEmail(String email) {
        return modelMapper
                .map(userRepository.findByEmail(email)
                                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, String.format("No user found with this email : %s", email)))
                        , UserDto.class);
    }

    @Transactional(readOnly = true)
    public List<AuthorityDto> findAllAuthorities() {
        List<Authority> authorities = Optional.of(authorityRepository.findAll())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Authority found"));

        return modelMapper.map(authorities, new TypeToken<List<AuthorityDto>>() {}.getType());
    }

    @Transactional
    public List<RoleDto> saveRoles(RoleMngRequestDto roleMngRequestDto){
        Map<Long, RoleDto> reqRolesMap = roleMngRequestDto.getAllRoles().stream().collect(Collectors.toMap(RoleDto::getId, role -> role));
        List<Role> originRoles = roleRepository.findAllById(reqRolesMap.keySet());

        for(Role originRole : originRoles){
            originRole.setRoleName(reqRolesMap.get(originRole.getId()).getRoleName());

            Optional.ofNullable(reqRolesMap.get(originRole.getId()).getAuthorities())
                    .ifPresentOrElse(
                            authorities -> originRole.setAuthorities(
                                    authorityRepository.findByAuthorityIn(
                                            authorities.stream()
                                                    .map(AuthorityDto::getAuthority)
                                                    .toList()
                                    )
                            ),
                            () -> originRole.setAuthorities(Collections.emptyList())
                    );
        }

        return modelMapper.map(originRoles, new TypeToken<List<RoleDto>>() {}.getType());
    }

    @PreAuthorize("hasAuthority(T(com.sptek._frameworkWebCore.springSecurity.AuthorityEnum).AUTH_SPECIAL_FOR_TEST)")
    public String iNeedAuth() {
        return "I Need Specific Auth";
    }

    @PreAuthorize("hasRole('ADMIN')")
    public String iNeedRole() {
        return "I Need Specific Role";
    }

}
