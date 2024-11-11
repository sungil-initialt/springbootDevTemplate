package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.common.code.BaseCode;
import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.config.springSecurity.AuthorityEnum;
import com.sptek.webfw.config.springSecurity.extras.dto.*;
import com.sptek.webfw.config.springSecurity.extras.entity.*;
import com.sptek.webfw.config.springSecurity.extras.repository.*;
import com.sptek.webfw.util.ModelMapperUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.spel.ast.OpNE;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TermsRepository termsRepository;
    private final TestRepository testRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User saveUser(SignupRequestDto signupRequestDto){
        List<RoleDto> roles = getRolesByRoleNameIn(signupRequestDto.getRoles().stream().map(RoleDto::getRoleName).collect(Collectors.toList()));
        List<TermsDto> terms = getTermsByTermsNameIn(signupRequestDto.getTerms().stream().map(TermsDto::getTermsName).collect(Collectors.toList()));

        signupRequestDto.setRoles(roles);
        signupRequestDto.setTerms(terms);
        signupRequestDto.setPassword(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()));
        User user = modelMapper.map(signupRequestDto, User.class);
        log.debug("new userEntity : {}", user);
        return userRepository.save(user);
    }

    public List<RoleDto> getAllRoles(){
        List<Role> roles = roleRepository.findAll();
        if(roles.isEmpty()) {
            throw new  ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Role found");
        }
        return modelMapper.map(roles, new TypeToken<List<RoleDto>>() {}.getType());

//        return Optional.ofNullable(roleRepository.findAll())
//                .filter(roles -> !roles.isEmpty())
//                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Role found"))
//                .stream()
//                .map(role -> modelMapper.map(role, RoleDto.class))
//                .collect(Collectors.toList());
    }

    public List<RoleDto> getRolesByRoleNameIn(List<String> roleNames){
        return modelMapper.map(
                roleRepository.findByRoleNameIn(roleNames).orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Role found"))
                , new TypeToken<List<RoleDto>>() {}.getType());
    }

    //for Test
    public List<RoleDto> getAllRolesX() {
        return Optional.of(roleRepository.findAll())
                .filter(roleList -> !roleList.isEmpty())
                .orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "No roles found"))
                .stream()
                .map(role -> modelMapper.map(role, RoleDto.class))
                .collect(Collectors.toList());
    }

    public List<TermsDto> getAllTerms(){
        return modelMapper.map(
                termsRepository.findAllAsOptional().orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Terms found"))
                , new TypeToken<List<TermsDto>>() {}.getType());
    }

    public List<TermsDto> getTermsByTermsNameIn(List<String> termsNames){
        return modelMapper.map(
                termsRepository.findByTermsNameIn(termsNames).orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Terms found"))
                , new TypeToken<List<TermsDto>>() {}.getType());
    }

    public UserDto getUserByEmail(String email) {
        return modelMapper.map(
                userRepository.findByEmail(email).orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, String.format("No user found with this email : %s", email)))
                , UserDto.class);
    }

    public List<AuthoritytDto> getAllAuthorities() {
        return modelMapper.map(
                authorityRepository.findAllAsOptional().orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "any Authority found"))
                , new TypeToken<List<AuthoritytDto>>() {}.getType());
    }

    public List<RoleDto> saveRoles(RoleMngRequestDto roleMngRequestDto){
        Map<Long, RoleDto> reqRolesMap = roleMngRequestDto.getAllRoles().stream().collect(Collectors.toMap(RoleDto::getId, role -> role));
        List<Role> orgRoles = roleRepository.findAllById(reqRolesMap.keySet());

        for(Role orgRole : orgRoles){
            orgRole.setRoleName(reqRolesMap.get(orgRole.getId()).getRoleName());

            Optional.ofNullable(reqRolesMap.get(orgRole.getId()).getAuthorities())
                    .ifPresentOrElse(authorities -> orgRole.setAuthorities(authorityRepository.findByAuthorityIn(authorities.stream().map(AuthoritytDto::getAuthority).toList()))
                    , () -> orgRole.setAuthorities(Collections.emptyList())
                    );
        }

        return modelMapper.map(roleRepository.saveAll(orgRoles), new TypeToken<List<RoleDto>>() {}.getType());
    }



    // TEST **************** 결론은 단일 반환의 경우 Optional 로 받고 복수는 List<T>로 받자 (조회값이 없더라도 empty List를 내리지 null 을 내리지 않음으로 Optional을 이중으로 할 필요가 없음)
    public Map<String, Object> testRepository(String key) {
        List<String> keys = Arrays.asList(key.split("-"));
        Map<String, Object> returnMap = new HashMap<>();

        if(keys.size() == 1){
            Test returnObj = testRepository.findByMyKey(keys.get(0));
            testRepositoryWithObj(returnObj);
            returnMap.put("returnObj", returnObj);

            Optional<Test> returnOpt = testRepository.findOptByMyKey(keys.get(0));
            testRepositoryWithOptional(returnOpt);
            returnMap.put("returnOpt", returnOpt);

        }else {
            List<Test> returnList = testRepository.findByMyKeyIn(keys);
            testRepositoryWithList(returnList);
            returnMap.put("returnList", returnList);

            Optional<List<Test>> returnListOpt = testRepository.findOptByMyKeyIn(keys);
            testRepositoryWithListOpt(returnListOpt);
            returnMap.put("returnListOpt", returnListOpt);
        }

        log.debug("strMap : {}", new HashMap<String, String>());
        return returnMap;
    }


    public void testRepositoryWithObj(Test test) {
        if(test == null) 
            log.debug("test obj is null");
        else 
            log.debug("test obj is not null : {}", test);

    }

    public void testRepositoryWithOptional(Optional<Test> testOpt) {
        //서로 반대 의미
        log.debug("isEmpty : {}" , testOpt.isEmpty());
        log.debug("isPresent : {}" , testOpt.isPresent());

        //조재하는 케이스만 처리
        testOpt.ifPresent(test -> log.debug("ifPresent : {}", test));

        //존재할때와 안할때 케이스 처리
        testOpt.ifPresentOrElse(
                test -> log.debug("ifpresendt : {}", test)
                ,() -> log.debug("ifPresentOrElse"));

        //존재하지 않을때의 대체값
        log.debug("orElse : {} ", testOpt.orElse(Test.builder().myKey("TEST NAME").build()));

        //존재하지 않을때의 Exception 처리
        try {
            testOpt.orElseThrow(() -> new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR));
        } catch (ServiceException ex) {
            log.debug(ex.getMessage());
        }

        try {
            log.debug("testOpt.get() : {}", testOpt.get()); //empty 상태 일때는 NoSuchElementException 발생
        } catch (NoSuchElementException ex) {
            log.debug(ex.getMessage());
        }
    }

    public void testRepositoryWithList(List<Test> tests) {
        if (tests == null){
            log.debug("tests is null"); //조회값이 없어도 List 를 내려줌 (empty 상태)
        } else {
            log.debug("tests is not null");
            
            if (tests.isEmpty()) {
                log.debug("But tests is empty"); //조회값이 없으면 empty List
            } else {
                log.debug("first test : {}", tests.get(0));
            }
        }
    }

    public void testRepositoryWithListOpt(Optional<List<Test>> testsOpt) {
        log.debug("isEmpty : {}" , testsOpt.isEmpty()); //조회값이 없더라도 항상 empty List를 가지고 있음으로 true, 내부 List는 empty 임
        log.debug("isPresent : {}" , testsOpt.isPresent());

        testsOpt.ifPresent(tests -> log.debug("ifPresent : {}", tests));

        testsOpt.ifPresentOrElse(
                tests -> log.debug("ifPresent : {}", tests)
                ,() -> log.debug("ifPresentOrElse"));
    }
}
