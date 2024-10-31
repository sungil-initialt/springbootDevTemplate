package com.sptek.webfw.example.springSecurity;

import com.sptek.webfw.config.springSecurity.extras.dto.SignupRequestDto;
import com.sptek.webfw.config.springSecurity.extras.dto.UserAddressDto;
import com.sptek.webfw.config.springSecurity.extras.dto.UserDto;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.config.springSecurity.extras.UserService;
import com.sptek.webfw.config.springSecurity.extras.repository.TestRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Slf4j
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class SecurityTestController {
    private final String pagePath = "pages/example/test/";
    @Autowired
    private UserService userService;


    @GetMapping("/signup") //회원가입 입력
    public String signup(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        //체크박스를 그리기 위한 용도
        model.addAttribute("allRoleNames", userService.getAllRoles());
        model.addAttribute("allTermsNames", userService.getAllTerms());

        log.debug("all Roles from db: {}", model.getAttribute("allRoleNames"));
        log.debug("all Terms from db: {}", model.getAttribute("allTermsNames"));

        List<UserAddressDto> userAddresses = new ArrayList<>();
        userAddresses.add(new UserAddressDto());
        userAddresses.add(new UserAddressDto());
        userAddresses.add(new UserAddressDto());
        /*
        userAddresses.add(UserAddressDto.builder().addressType("").address("").build());
        userAddresses.add(UserAddressDto.builder().addressType("").address("").build());
        userAddresses.add(UserAddressDto.builder().addressType("").address("").build());
        */

        signupRequestDto.setUserAddresses(userAddresses);
        log.debug("all signupRequestDto : {}", signupRequestDto);
        //model.addAttribute("signupRequestDto", signupRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return pagePath + "signup";
    }

    @PostMapping("/signup") //회원가입 처리
    public String signupWithValidation(Model model, RedirectAttributes redirectAttributes, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        //에러케이스 처리 : signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 그리기 위한 용도
            model.addAttribute("allRoleNames", userService.getAllRoles());
            model.addAttribute("allTermsNames", userService.getAllTerms());
            return pagePath + "signup";
        }
        //signupRequestDto.getUserAddresses().stream().forEach(userAddress -> log.debug("userAddress : {}", userAddress.toString()));
        User savedUser = userService.saveUser(signupRequestDto);

        //model.addAttribute("userName", savedUser.getName()); //redirect 시에는 전달되지 않음
        //redirectAttributes.addAttribute("userName", savedUser.getName()); // url 쿼리 string에 반영되어 전달
        redirectAttributes.addFlashAttribute("userName", savedUser.getName()); //redirect 페이지로 1회성으로 전달됨

        //return pagePath + "signin";
        return "redirect:/signin";
    }

    @GetMapping("/signin") //로그인 입력
    public String login(Model model , SignupRequestDto signupRequestDto) {
        //log.debug("redirectAttributes(userName) : {}", model.getAttribute("userName"));
        return pagePath + "signin";
    }

    @GetMapping("/user/{email}")
    public String user(@PathVariable("email") String email, Model model) {
        UserDto resultUserDto = userService.getUserByEmail(email);
        log.debug("{} user info search result : {}", email, resultUserDto);

        model.addAttribute("result", resultUserDto);
        return pagePath + "simpleModelView";
    }

    @GetMapping("/user/mypage")
    public String user(Model model) {
        String myAuthInfo = SecurityContextHolder.getContext().getAuthentication().toString();
        log.debug("my Auth : {}", myAuthInfo);

        model.addAttribute("result", myAuthInfo);
        return pagePath + "simpleModelView";
    }

    @GetMapping("/user/test/{key}")
    public String test(@PathVariable("key") String key, Model model) {
        Map<String, Object> resultMap = userService.testRepository(key);
        model.addAttribute("result", resultMap);
        return pagePath + "simpleModelView";
    }
}
