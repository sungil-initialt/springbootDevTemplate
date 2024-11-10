package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.AuthorityEnum;
import com.sptek.webfw.config.springSecurity.extras.dto.*;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.util.ModelMapperUtil;
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
        //화면에 그리기 위한 값들
        signupRequestDto.setUserAddresses(List.of(new UserAddressDto()));
        signupRequestDto.setAllRoles(userService.getAllRoles());
        signupRequestDto.setAllTerms(userService.getAllTerms());

        //model.addAttribute("signupRequestDto", signupRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return pagePath + "signup";
    }

    @PostMapping("/signup") //회원가입 처리
    public String signupWithValidation(Model model, RedirectAttributes redirectAttributes, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            signupRequestDto.setAllRoles(userService.getAllRoles());
            signupRequestDto.setAllTerms(userService.getAllTerms());
            return pagePath + "signup";
        }

        User savedUser = userService.saveUser(signupRequestDto);
        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        redirectAttributes.addFlashAttribute("userName", savedUser.getName()); //redirect 페이지에 model을 보내기 위해 addFlashAttribute 사용(1회성으로 전달됨), addAttribute를 사용지 쿼리 스트링을 통해 전달됨.
        return "redirect:/signin";
    }

    @GetMapping("/signin") //로그인 입력
    public String login(Model model , SignupRequestDto signupRequestDto) {
        return pagePath + "signin";
    }

    @GetMapping("/user/{email}")
    public String user(@PathVariable("email") String email, Model model) {
        UserDto resultUserDto = userService.getUserByEmail(email);
        model.addAttribute("result", resultUserDto);
        return pagePath + "simpleModelView";
    }

    @GetMapping("/user/mypage")
    public String user(Model model) {
        String myAuthInfo = SecurityContextHolder.getContext().getAuthentication().toString();
        model.addAttribute("result", myAuthInfo);
        return pagePath + "simpleModelView";
    }



    @GetMapping("/roles")
    public String roles(Model model, RoleMngRequestDto roleMngRequestDto) {
        roleMngRequestDto.setAllRoles(userService.getAllRoles());
        roleMngRequestDto.setAllAuthorities(userService.getAllAuthorities());
        return pagePath + "roles";
    }

    @PostMapping("/roles")
    public String roleWithAuthority(Model model, RedirectAttributes redirectAttributes, @Valid RoleMngRequestDto roleMngRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            roleMngRequestDto.setAllRoles(userService.getAllRoles());
            roleMngRequestDto.setAllAuthorities(userService.getAllAuthorities());
            return pagePath + "roles";
        }

        //User savedUser = userService.saveUser(signupRequestDto);
        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        //redirectAttributes.addFlashAttribute("userName", savedUser.getName()); //redirect 페이지에 model을 보내기 위해 addFlashAttribute 사용(1회성으로 전달됨), addAttribute를 사용지 쿼리 스트링을 통해 전달됨.
        return "redirect:/roles";
    }


    //for test
    @GetMapping("/user/test/{key}")
    public String test(@PathVariable("key") String key, Model model) {
        Map<String, Object> resultMap = userService.testRepository(key);
        model.addAttribute("result", resultMap);
        return pagePath + "simpleModelView";
    }

    //for test
    @GetMapping("/user/test")
    public String test(Model model) {
        AuthorityEnum authority = AuthorityEnum.AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY;
        AuthoritytDto authDto = ModelMapperUtil.map(authority, AuthoritytDto.class);
        model.addAttribute("result", authDto);
        return pagePath + "simpleModelView";
    }

}
