package com.sptek.webfw.example.springSecurity;

import com.sptek.webfw.config.springSecurity.UserRoleEnum;
import com.sptek.webfw.config.springSecurity.extras.SignupRequestDto;
import com.sptek.webfw.config.springSecurity.extras.User;
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

@Slf4j
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class SecurityTestController {
    private final String PAGE_BASE_PATH = "pages/example/test/";
    @Autowired
    private UserService userService;


    @GetMapping("/signup") //회원가입 입력 페이지
    public String validationWithBindingResult(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        model.addAttribute("allUserRoles", Arrays.asList(UserRoleEnum.values()));
        return PAGE_BASE_PATH + "signup";
    }

    @PostMapping("/signup") //회원가입 처리
    public String validationWithBindingResult(Model model, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        model.addAttribute("allUserRoles", Arrays.asList(UserRoleEnum.values()));

        if (bindingResult.hasErrors()) {
            return PAGE_BASE_PATH + "signup";
        }

        userService.saveUser(signupRequestDto);
        return "redirect:" + "login";
    }

    @GetMapping("/User/{email}")
    public String user(@PathVariable String email, Model model) {
        User resultUser = userService.getUserByEmail(email);
        log.debug("{} user info search result : {}", email, resultUser);

        model.addAttribute("result", resultUser);
        return PAGE_BASE_PATH + "simpleModelView";
    }

    @GetMapping("/User/mypage")
    public String user(Model model) {
        String myAuthInfo = SecurityContextHolder.getContext().getAuthentication().toString();
        log.debug("my Auth : {}", myAuthInfo);

        model.addAttribute("result", myAuthInfo);
        return PAGE_BASE_PATH + "simpleModelView";
    }
}
