package com.sptek.webfw.example.springSecurity;

import com.sptek.webfw.config.springSecurity.UserRole;
import com.sptek.webfw.config.springSecurity.service.SignupRequestDto;
import com.sptek.webfw.config.springSecurity.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Slf4j
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class SecurityController {
    private final String PAGE_BASE_PATH = "pages/example/test/";
    @Autowired
    private UserService userService;


    @GetMapping("/signup")
    public String validationWithBindingResult(Model model, SignupRequestDto signupRequestDto) { //thyleaf 쪽에 default 값을 만들기 위해 signupRequestDto 필요함
        model.addAttribute("allUserRoles", Arrays.asList(UserRole.values()));
        return PAGE_BASE_PATH + "signup";
    }

    @PostMapping("/signup")
    public String validationWithBindingResult(Model model, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {
        model.addAttribute("allUserRoles", Arrays.asList(UserRole.values()));

        if (bindingResult.hasErrors()) {
            return PAGE_BASE_PATH + "signup";
        }
        if (StringUtils.hasText(signupRequestDto.getEmail()) && signupRequestDto.getEmail().contains("@naver.com")) {
            bindingResult.rejectValue("email", "emailFail", "네이버 메일은 사용할 수 없습니다.");
            return PAGE_BASE_PATH + "signup";
        }

        //do what you want.
        return "redirect:" + "login";
    }
}
