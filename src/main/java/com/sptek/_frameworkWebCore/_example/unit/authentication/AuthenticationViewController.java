package com.sptek._frameworkWebCore._example.unit.authentication;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfViewGlobalException_InViewController;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.*;
import com.sptek._frameworkWebCore.springSecurity.extras.entity.User;
import com.sptek._frameworkWebCore.util.SecurityUtil;
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

//signup 과 관련한 부분 들도 실제 서비스 환경 에서 변경 사항이 많은 영역 이라 example 영역에 둠 (login 은 systemSuport 영역에 둠)

public class AuthenticationViewController {

    @NonFinal
    private final String htmlBasePath = "pages/_example/unit/";
    private final ModelMapper modelMapper;
    private final AuthenticationService authenticationService;


    @GetMapping("/authentication/signupForm")
    public String signupForm(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        //화면에 그리기 위한 값들
        signupRequestDto.setUserAddresses(List.of(new UserAddressDto()));
        signupRequestDto.setAllRoles(authenticationService.findAllRoles());
        signupRequestDto.setAllTerms(authenticationService.findAllTerms());

        //model.addAttribute("signupRequestDto", signupRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return htmlBasePath + "signup";
    }

    @PostMapping("/authentication/signup")
    public String signup(Model model, RedirectAttributes redirectAttributes, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            signupRequestDto.setAllRoles(authenticationService.findAllRoles());
            signupRequestDto.setAllTerms(authenticationService.findAllTerms());
            return htmlBasePath + "signup";
        }
        User savedUser = authenticationService.saveUser(signupRequestDto);

        //redirect 페이지에 model을 보내기 위해 addFlashAttribute 사용(1회성으로 전달됨)
        redirectAttributes.addFlashAttribute("username", savedUser.getName());

        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        return "redirect:/view/login";
    }

    @GetMapping("/authentication/curAuthentication")
    public String curAuthentication(Model model) {
        String curAuthentication = SecurityUtil.getMyAuthentication().toString();
        //curAuthentication 내 RemoteIpAddress는 로그인을 요청한 ip주소, SessionId는 로그인 을 요청 했던 당시의 세션값(로그인 이후 새 값으로 변경됨)

        model.addAttribute("result", curAuthentication);
        return htmlBasePath + "simpleModelView";
    }

    @GetMapping("/login/authentication/userInfoView/{email}")
    @PreAuthorize("#email == authentication.principal.userDto.email  or  hasRole('ADMIN')")
    public String userInfoView(@PathVariable("email") String email, Model model) {
        UserDto resultUserDto = authenticationService.findUserByEmail(email);
        model.addAttribute("result", resultUserDto);
        return htmlBasePath + "simpleModelView";
    }

    @GetMapping("/login/authentication/userUpdateForm/{email}")
    //hasRole 과 hasAuthority 차이는 둘다 Authentication 의 authorities 에서 찾는데 hasRole('USER') 은 내부적으로 ROLE_USER 처럼 ROLE_ 를 붙여서 찾고 hasAuthority 는 그대로 찾는다.
    @PreAuthorize("hasAuthority(T(com.sptek._frameworkWebCore.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST) or #email == authentication.principal.userDto.email")
    public String userUpdateForm(@PathVariable("email") String email, Model model , UserUpdateRequestDto userUpdateRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        UserDto userDto = authenticationService.findUserByEmail(email);
        userUpdateRequestDto = modelMapper.map(userDto, UserUpdateRequestDto.class);
        userUpdateRequestDto.setPassword("");

        //화면에 그리기 위한 값들
        userUpdateRequestDto.setAllRoles(authenticationService.findAllRoles());
        userUpdateRequestDto.setAllTerms(authenticationService.findAllTerms());

        model.addAttribute("userUpdateRequestDto", userUpdateRequestDto);
        return htmlBasePath + "userUpdate";
    }

    @PostMapping("/login/authentication/userUpdate")
    @PreAuthorize("hasAuthority(T(com.sptek._frameworkWebCore.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST) or #userUpdateRequestDto.email == authentication.principal.userDto.email")
    public String userUpdate(Model model, RedirectAttributes redirectAttributes, @Valid UserUpdateRequestDto userUpdateRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            userUpdateRequestDto.setAllRoles(authenticationService.findAllRoles());
            userUpdateRequestDto.setAllTerms(authenticationService.findAllTerms());

            return htmlBasePath + "userUpdate";
        }
        User savedUser = authenticationService.updateUser(userUpdateRequestDto);

        redirectAttributes.addFlashAttribute("userEmail", savedUser.getEmail());
        return "redirect:/view/example/login/authentication/userUpdateForm/" + userUpdateRequestDto.getEmail();
    }

    @GetMapping("/role-system/authentication/roleUpdateForm")
    public String roleUpdateForm(Model model, RoleMngRequestDto roleMngRequestDto) {
        roleMngRequestDto.setAllRoles(authenticationService.findAllRoles());
        roleMngRequestDto.setAllAuthorities(authenticationService.findAllAuthorities());
        return htmlBasePath + "role";
    }

    @PostMapping("/role-system/authentication/roleUpdate")
    public String roleUpdate(Model model, RedirectAttributes redirectAttributes, @Valid RoleMngRequestDto roleMngRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            roleMngRequestDto.setAllRoles(authenticationService.findAllRoles());
            roleMngRequestDto.setAllAuthorities(authenticationService.findAllAuthorities());
            return htmlBasePath + "role";
        }

        authenticationService.saveRoles(roleMngRequestDto);
        return "redirect:/view/example/role-system/authentication/roleUpdateForm";
    }


}
