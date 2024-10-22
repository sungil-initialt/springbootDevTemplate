package com.sptek.webfw.example.springSecurity;

import com.sptek.webfw.config.springSecurity.RoleEnum;
import com.sptek.webfw.config.springSecurity.extras.SignupRequestDto;
import com.sptek.webfw.config.springSecurity.extras.User;
import com.sptek.webfw.config.springSecurity.extras.UserEntity;
import com.sptek.webfw.config.springSecurity.extras.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class SecurityTestController {
    private final String baseUrl = "pages/example/test/";
    @Autowired
    private UserService userService;


    @GetMapping("/signup") //회원가입 입력
    public String signup(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        signupRequestDto.setRoleNameList(userService.getAllRoles());
        signupRequestDto.setTermsNameList(userService.getAllTerms());

        log.debug("all Roles : {}", userService.getAllRoles());
        log.debug("all Terms : {}", userService.getAllTerms());

        //signupRequestDto.setRoleNameList(Arrays.stream(RoleEnum.values())
        //        .map(RoleEnum::getValue)
        //        .collect(Collectors.toList()));

        model.addAttribute("signupRequestDto", signupRequestDto);
        return baseUrl + "signup";
    }

    @PostMapping("/signup") //회원가입 처리
    public String signupWithValidation(Model model, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            return baseUrl + "signup";
        }

        UserEntity savedUserEntity = userService.saveUser(signupRequestDto);
        model.addAttribute("userName", savedUserEntity.getName());
        //model.addAttribute("userRoleList", savedUserEntity.getRoleEntitySet().stream().map(roleEntity -> roleEntity.getRoleEnum().getValue()).collect(Collectors.toList()));
        return baseUrl + "signin";
    }

    @GetMapping("/signin") //로그인 입력
    public String login(Model model , SignupRequestDto signupRequestDto) {
        return baseUrl + "signin";
    }

    @GetMapping("/User/{email}")
    public String user(@PathVariable String email, Model model) {
        User resultUser = userService.getUserByEmail(email);
        log.debug("{} user info search result : {}", email, resultUser);

        model.addAttribute("result", resultUser);
        return baseUrl + "simpleModelView";
    }

    @GetMapping("/User/mypage")
    public String user(Model model) {
        String myAuthInfo = SecurityContextHolder.getContext().getAuthentication().toString();
        log.debug("my Auth : {}", myAuthInfo);

        model.addAttribute("result", myAuthInfo);
        return baseUrl + "simpleModelView";
    }
}
