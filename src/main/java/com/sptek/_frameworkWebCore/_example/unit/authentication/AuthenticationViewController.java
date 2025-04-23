package com.sptek._frameworkWebCore._example.unit.authentication;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfViewGlobalException_InViewController;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.*;
import com.sptek._frameworkWebCore.springSecurity.extras.entity.User;
import com.sptek._frameworkWebCore.springSecurity.test.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor
@Controller
@EnableResponseOfViewGlobalException_InViewController
@RequestMapping(value = "/view/example/", produces = MediaType.TEXT_HTML_VALUE)

public class AuthenticationViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";
    private final ModelMapper modelMapper;
    private final SecurityService securityService;


    @GetMapping("/authentication/signupForm")
    public String signupForm(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        //화면에 그리기 위한 값들
        signupRequestDto.setUserAddresses(List.of(new UserAddressDto()));
        signupRequestDto.setAllRoles(securityService.findAllRoles());
        signupRequestDto.setAllTerms(securityService.findAllTerms());

        //model.addAttribute("signupRequestDto", signupRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return htmlBasePath + "signup";
    }

    @PostMapping("/authentication/signup")
    public String signup(Model model, RedirectAttributes redirectAttributes, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            signupRequestDto.setAllRoles(securityService.findAllRoles());
            signupRequestDto.setAllTerms(securityService.findAllTerms());
            return htmlBasePath + "signup";
        }
        User savedUser = securityService.saveUser(signupRequestDto);

        //redirect 페이지에 model을 보내기 위해 addFlashAttribute 사용(1회성으로 전달됨)
        redirectAttributes.addFlashAttribute("username", savedUser.getName());

        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        return "redirect:/view/login";
    }

    @GetMapping("/secured-Any-Role/authentication/userInfoView/{email}")
    @PreAuthorize("(#email == authentication.principal.userDto.email)"
            + "|| hasRole('ADMIN')"
    )
    public String userInfoView(@PathVariable("email") String email, Model model) {
        UserDto resultUserDto = securityService.findUserByEmail(email);
        model.addAttribute("result", resultUserDto);
        return htmlBasePath + "simpleModelView";
    }

    @GetMapping("/secured-Any-Role/authentication/userUpdateForm/{email}")
    //hasRole 과 hasAuthority 차이는 둘다 Authentication 의 authorities 에서 찾는데 hasRole('USER') 은 내부적으로 ROLE_USER 처럼 ROLE_ 를 붙여서 찾고 hasAuthority 는 그대로 찾는다.
    @PreAuthorize("hasAuthority(T(com.sptek._frameworkWebCore.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST)"
            + "|| #email == authentication.principal.userDto.email"
    )
    public String userUpdateForm(@PathVariable("email") String email, Model model , UserUpdateRequestDto userUpdateRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        UserDto userDto = securityService.findUserByEmail(email);
        userUpdateRequestDto = modelMapper.map(userDto, UserUpdateRequestDto.class);
        userUpdateRequestDto.setPassword("");

        //화면에 그리기 위한 값들
        userUpdateRequestDto.setAllRoles(securityService.findAllRoles());
        userUpdateRequestDto.setAllTerms(securityService.findAllTerms());

        model.addAttribute("userUpdateRequestDto", userUpdateRequestDto);
        return htmlBasePath + "userUpdate";
    }

    @PostMapping("/secured-Any-Role/authentication/userUpdate")
    @PreAuthorize("hasAuthority(T(com.sptek._frameworkWebCore.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST)"
            + "|| #userUpdateRequestDto.email == authentication.principal.userDto.email"
    )
    public String userUpdate(Model model, RedirectAttributes redirectAttributes, @Valid UserUpdateRequestDto userUpdateRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            userUpdateRequestDto.setAllRoles(securityService.findAllRoles());
            userUpdateRequestDto.setAllTerms(securityService.findAllTerms());

            return htmlBasePath + "userUpdate";
        }
        User savedUser = securityService.updateUser(userUpdateRequestDto);

        redirectAttributes.addFlashAttribute("userEmail", savedUser.getEmail());
        return "redirect:/view/example/secured-Any-Role/authentication/userUpdateForm/" + userUpdateRequestDto.getEmail();
    }

    @GetMapping("/secured-System-Role/authentication/roleUpdateForm")
    public String roleUpdateForm(Model model, RoleMngRequestDto roleMngRequestDto) {
        roleMngRequestDto.setAllRoles(securityService.findAllRoles());
        roleMngRequestDto.setAllAuthorities(securityService.findAllAuthorities());
        return htmlBasePath + "role";
    }

    @PostMapping("/secured-System-Role/authentication/roleUpdate")
    public String roleUpdate(Model model, RedirectAttributes redirectAttributes, @Valid RoleMngRequestDto roleMngRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            roleMngRequestDto.setAllRoles(securityService.findAllRoles());
            roleMngRequestDto.setAllAuthorities(securityService.findAllAuthorities());
            return htmlBasePath + "role";
        }

        securityService.saveRoles(roleMngRequestDto);
        return "redirect:/view/example/secured-System-Role/authentication/roleUpdateForm";
    }

}
