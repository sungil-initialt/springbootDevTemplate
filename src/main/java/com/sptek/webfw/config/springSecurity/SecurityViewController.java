package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.extras.dto.*;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.config.springSecurity.spt.LoginHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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


    @GetMapping("/signup")
    public String signup(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        //화면에 그리기 위한 값들
        signupRequestDto.setUserAddresses(List.of(new UserAddressDto()));
        signupRequestDto.setAllRoleAuthorities(securityService.findAllRoles());
        signupRequestDto.setAllTerms(securityService.findAllTerms());

        //model.addAttribute("signupRequestDto", signupRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return pagePath + "signup";
    }

    @PostMapping("/signup")
    public String signupWithValidation(Model model, RedirectAttributes redirectAttributes, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            signupRequestDto.setAllRoleAuthorities(securityService.findAllRoles());
            signupRequestDto.setAllTerms(securityService.findAllTerms());
            return pagePath + "signup";
        }
        User savedUser = securityService.saveUser(signupRequestDto);

        //redirect 페이지에 model을 보내기 위해 addFlashAttribute 사용(1회성으로 전달됨)
        redirectAttributes.addFlashAttribute("userName", savedUser.getName());

        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model , LoginRequestDto loginRequestDto, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("redirectTo", LoginHelper.getRedirectUrlAfterLogging(request, response));
        return pagePath + "login";
    }

    @PreAuthorize("(#email == authentication.principal.userDto.email) || hasRole('ADMIN')")
    @GetMapping({"/user/{email}"
            , "/admin/{email}"
            , "/system/{email}"})
    public String user(@PathVariable("email") String email, Model model) {
        UserDto resultUserDto = securityService.findUserByEmail(email);
        model.addAttribute("result", resultUserDto);
        return pagePath + "simpleModelView";
    }

    //hasRole 과 hasAuthority 차이는 둘다 Authentication 의 authorities 에서 찾는데 hasRole('USER') 은 내부적으로 ROLE_USER 처럼 ROLE_ 를 붙여서 찾고 hasAuthority 는 그대로 찾는다.
    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_RETRIEVE_USER_ALL_FOR_MARKETING) "
                    + "|| #email == authentication.principal.userDto.email"
    )
    @GetMapping("/user/update/{email}")
    public String userUpdate(@PathVariable("email") String email, Model model , UserUpdateRequestDto userUpdateRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        UserDto userDto = securityService.findUserByEmail(email);
        userUpdateRequestDto = modelMapper.map(userDto, UserUpdateRequestDto.class);
        userUpdateRequestDto.setPassword("");

        //화면에 그리기 위한 값들
        userUpdateRequestDto.setAllRoleAuthorities(securityService.findAllRoles());
        userUpdateRequestDto.setAllTerms(securityService.findAllTerms());

        model.addAttribute("userUpdateRequestDto", userUpdateRequestDto);
        return pagePath + "userUpdate";
    }

    @PreAuthorize(
            "hasAuthority('AUTH_RETRIEVE_USER_ALL_FOR_MARKETING') "
                    + "|| #userUpdateRequestDto.email == authentication.principal.userDto.email"
    )
    @PostMapping("/user/update")
    public String signupWithValidation(Model model, RedirectAttributes redirectAttributes, @Valid UserUpdateRequestDto userUpdateRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            userUpdateRequestDto.setAllRoleAuthorities(securityService.findAllRoles());
            userUpdateRequestDto.setAllTerms(securityService.findAllTerms());

            return pagePath + "userUpdate";
        }
        User savedUser = securityService.updateUser(userUpdateRequestDto);

        redirectAttributes.addFlashAttribute("userEmail", savedUser.getEmail());
        return "redirect:/user/update/" + userUpdateRequestDto.getEmail();
    }

    @GetMapping("/roles")
    public String roles(Model model, RoleMngRequestDto roleMngRequestDto) {
        roleMngRequestDto.setAllRoleAuthorities(securityService.findAllRoles());
        roleMngRequestDto.setAllAuthorities(securityService.findAllAuthorities());
        return pagePath + "roles";
    }

    @PostMapping("/roles")
    public String roleWithAuthority(Model model, RedirectAttributes redirectAttributes, @Valid RoleMngRequestDto roleMngRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            roleMngRequestDto.setAllRoleAuthorities(securityService.findAllRoles());
            roleMngRequestDto.setAllAuthorities(securityService.findAllAuthorities());
            return pagePath + "roles";
        }

        securityService.saveRoles(roleMngRequestDto);
        return "redirect:/roles";
    }

    @PostMapping("/role/createRole")
    public String makeNewRole(Model model, RedirectAttributes redirectAttributes, @Valid RoleMngRequestDto roleMngRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            roleMngRequestDto.setAllRoleAuthorities(securityService.findAllRoles());
            roleMngRequestDto.setAllAuthorities(securityService.findAllAuthorities());
            return pagePath + "roles";
        }

        securityService.saveRoles(roleMngRequestDto);
        return "redirect:/roles";
    }

    @GetMapping("/my/mypage")
    public String mypage(Model model) {
        String myAuthentication = SecurityContextHolder.getContext().getAuthentication().toString();
        //myAuthentication 내 RemoteIpAddress는 로그인을 요청한 ip주소, SessionId는 로그인을 요청했던 당시의 세션값(로그인 이후 새 값으로 변경됨)

        model.addAttribute("result", myAuthentication);
        return pagePath + "simpleModelView";
    }

    @GetMapping("/admin/anyone/default")
    public String adminDefault(Model model) {
        model.addAttribute("result", "Admin default page for admin anyone");
        return pagePath + "simpleModelView";
    }

    @GetMapping("/admin/marketing/event")
    public String adminMarketing(Model model) {
        model.addAttribute("result", "Admin marketing event page for only the admin who has AUTH_RETRIEVE_USER_ALL_FOR_MARKETING");
        return pagePath + "simpleModelView";
    }

    @GetMapping("/system/env")
    public String systemEnv(Model model) {
        model.addAttribute("result", "system env page for only the system admin");
        return pagePath + "simpleModelView";
    }
}
