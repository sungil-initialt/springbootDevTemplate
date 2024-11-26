package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.extras.dto.*;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class SecurityViewController {

    private final String pagePath = "pages/example/test/";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SecurityService securityService;


    @GetMapping("/signup") //회원가입 입력
    public String signup(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        //화면에 그리기 위한 값들
        signupRequestDto.setUserAddresses(List.of(new UserAddressDto()));
        signupRequestDto.setAllRoles(securityService.findAllRoles());
        signupRequestDto.setAllTerms(securityService.findAllTerms());

        //model.addAttribute("signupRequestDto", signupRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return pagePath + "signup";
    }

    @PostMapping("/signup") //회원가입 처리
    public String signupWithValidation(Model model, RedirectAttributes redirectAttributes, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            signupRequestDto.setAllRoles(securityService.findAllRoles());
            signupRequestDto.setAllTerms(securityService.findAllTerms());
            return pagePath + "signup";
        }

        User savedUser = securityService.saveUser(signupRequestDto);
        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        redirectAttributes.addFlashAttribute("userName", savedUser.getName()); //redirect 페이지에 model을 보내기 위해 addFlashAttribute 사용(1회성으로 전달됨), addAttribute를 사용지 쿼리 스트링을 통해 전달됨.
        return "redirect:/signin";
    }

    @GetMapping("/signin") //로그인 입력
    public String signin(Model model , SigninRequestDto signinRequestDto) {
        return pagePath + "signin";
    }

    @GetMapping("/user/{email}")
    public String user(@PathVariable("email") String email, Model model) {
        UserDto resultUserDto = securityService.findUserByEmail(email);
        model.addAttribute("result", resultUserDto);
        return pagePath + "simpleModelView";
    }

    @GetMapping("/user/update/{email}")
    public String userUpdate(@PathVariable("email") String email, Model model , UserUpdateRequestDto userUpdateRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        UserDto userDto = securityService.findUserByEmail(email);
        userUpdateRequestDto = modelMapper.map(userDto, UserUpdateRequestDto.class);
        userUpdateRequestDto.setPassword("");

        //화면에 그리기 위한 값들
        userUpdateRequestDto.setAllRoles(securityService.findAllRoles());
        userUpdateRequestDto.setAllTerms(securityService.findAllTerms());

        model.addAttribute("userUpdateRequestDto", userUpdateRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return pagePath + "userUpdate";
    }

    @PostMapping("/user/update") //회원가입 처리
    public String signupWithValidation(Model model, RedirectAttributes redirectAttributes, @Valid UserUpdateRequestDto userUpdateRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            userUpdateRequestDto.setAllRoles(securityService.findAllRoles());
            userUpdateRequestDto.setAllTerms(securityService.findAllTerms());

            return pagePath + "userUpdate";
        }

        User savedUser = securityService.updateUser(userUpdateRequestDto);
        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        redirectAttributes.addFlashAttribute("userName", savedUser.getEmail());
        return "redirect:/user/update/" + userUpdateRequestDto.getEmail();
    }

    @GetMapping("/roles")
    public String roles(Model model, RoleMngRequestDto roleMngRequestDto) {
        roleMngRequestDto.setAllRoles(securityService.findAllRoles());
        roleMngRequestDto.setAllAuthorities(securityService.findAllAuthorities());
        return pagePath + "roles";
    }

    @PostMapping("/roles")
    public String roleWithAuthority(Model model, RedirectAttributes redirectAttributes, @Valid RoleMngRequestDto roleMngRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            roleMngRequestDto.setAllRoles(securityService.findAllRoles());
            roleMngRequestDto.setAllAuthorities(securityService.findAllAuthorities());
            return pagePath + "roles";
        }

        securityService.saveRoles(roleMngRequestDto);
        return "redirect:/roles";
    }

    @GetMapping("/my/mypage")
    public String mypage(Model model) {
        String myContextAuthentication = SecurityContextHolder.getContext().getAuthentication().toString();
        //myContextAuthentication 내 RemoteIpAddress는 로그인을 요청한 ip주소, SessionId는 로그인을 요청했던 당시의 세션값(로그인 이후 새 값으로 변경됨)

        model.addAttribute("result", myContextAuthentication);
        return pagePath + "simpleModelView";
    }

    @GetMapping("/admin/anyone/default")
    public String adminDefault(Model model) {
        model.addAttribute("result", "Admin default page for admin anyone");
        return pagePath + "simpleModelView";
    }

    @GetMapping("/admin/marketing/event")
    public String adminMarketing(Model model) {
        model.addAttribute("result", "Admin marketing event page for only admin who has AUTH_RETRIEVE_USER_ALL_FOR_MARKETING");
        return pagePath + "simpleModelView";
    }

}
