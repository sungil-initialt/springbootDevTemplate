package com.sptek.webfw.config.springSecurity.test;

import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.config.springSecurity.extras.dto.*;
import com.sptek.webfw.config.springSecurity.extras.entity.User;
import com.sptek.webfw.config.springSecurity.spt.LoginHelper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequiredArgsConstructor
@Controller
@RequestMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
public class SecurityViewController {

    @NonFinal
    private final String pagePath = "pages/example/test/";
    private final ModelMapper modelMapper;
    private final SecurityService securityService;

    @GetMapping("/signup")
    public String signup(Model model , SignupRequestDto signupRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        //화면에 그리기 위한 값들
        signupRequestDto.setUserAddresses(List.of(new UserAddressDto()));
        signupRequestDto.setAllRoles(securityService.findAllRoles());
        signupRequestDto.setAllTerms(securityService.findAllTerms());

        //model.addAttribute("signupRequestDto", signupRequestDto); //파람에 들어 있음으로 addAttribute 불필요
        return pagePath + "signup";
    }

    @PostMapping("/signup")
    public String signupWithValidation(Model model, RedirectAttributes redirectAttributes, @Valid SignupRequestDto signupRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            signupRequestDto.setAllRoles(securityService.findAllRoles());
            signupRequestDto.setAllTerms(securityService.findAllTerms());
            return pagePath + "signup";
        }
        User savedUser = securityService.saveUser(signupRequestDto);

        //redirect 페이지에 model을 보내기 위해 addFlashAttribute 사용(1회성으로 전달됨)
        redirectAttributes.addFlashAttribute("username", savedUser.getName());

        //저장 후 페이지 뒤로가기에서 데이터를 다시 전달하려 하는것을 막기위해 redirect를 사용함
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model , HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("redirectTo", LoginHelper.getRedirectUrlAfterLogging(request, response));
        return pagePath + "login";
    }

    @PreAuthorize("(#email == authentication.principal.userDto.email) || hasRole('ADMIN')")
    @GetMapping("/auth/user/info/{email}")
    public String user(@PathVariable("email") String email, Model model) {
        UserDto resultUserDto = securityService.findUserByEmail(email);
        model.addAttribute("result", resultUserDto);
        return pagePath + "simpleModelView";
    }

    //hasRole 과 hasAuthority 차이는 둘다 Authentication 의 authorities 에서 찾는데 hasRole('USER') 은 내부적으로 ROLE_USER 처럼 ROLE_ 를 붙여서 찾고 hasAuthority 는 그대로 찾는다.
    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST)"
                    + "|| #email == authentication.principal.userDto.email"
    )
    @GetMapping("/auth/user/update/{email}")
    public String userUpdate(@PathVariable("email") String email, Model model , UserUpdateRequestDto userUpdateRequestDto) { //thyleaf 쪽에서 입력 항목들의 default 값을 넣어주기 위해 signupRequestDto 필요함
        UserDto userDto = securityService.findUserByEmail(email);
        userUpdateRequestDto = modelMapper.map(userDto, UserUpdateRequestDto.class);
        userUpdateRequestDto.setPassword("");

        //화면에 그리기 위한 값들
        userUpdateRequestDto.setAllRoles(securityService.findAllRoles());
        userUpdateRequestDto.setAllTerms(securityService.findAllTerms());

        model.addAttribute("userUpdateRequestDto", userUpdateRequestDto);
        return pagePath + "userUpdate";
    }

    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST)"
                    + "|| #userUpdateRequestDto.email == authentication.principal.userDto.email"
    )
    @PostMapping("/auth/user/update")
    public String signupWithValidation(Model model, RedirectAttributes redirectAttributes, @Valid UserUpdateRequestDto userUpdateRequestDto, BindingResult bindingResult) {

        //signupRequestDto 에 바인딩 하는 과정에서 에러가 있는 경우
        if (bindingResult.hasErrors()) {
            //체크박스를 다시 그리기 위해
            userUpdateRequestDto.setAllRoles(securityService.findAllRoles());
            userUpdateRequestDto.setAllTerms(securityService.findAllTerms());

            return pagePath + "userUpdate";
        }
        User savedUser = securityService.updateUser(userUpdateRequestDto);

        redirectAttributes.addFlashAttribute("userEmail", savedUser.getEmail());
        return "redirect:/auth/user/update/" + userUpdateRequestDto.getEmail();
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
        String myAuthentication = SecurityContextHolder.getContext().getAuthentication().toString();
        //myAuthentication 내 RemoteIpAddress는 로그인을 요청한 ip주소, SessionId는 로그인을 요청했던 당시의 세션값(로그인 이후 새 값으로 변경됨)

        model.addAttribute("result", myAuthentication);
        return pagePath + "simpleModelView";
    }

    @GetMapping("/admin/anyone")
    public String adminAnyone(Model model) {
        model.addAttribute("result", "adminAnyone page for admin anyone");
        return pagePath + "simpleModelView";
    }

    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_RETRIEVE_USER_ALL_FOR_MARKETING)"
    )
    @GetMapping("/admin/marketing")
    public String adminMarketing(Model model) {
        model.addAttribute("result", "adminMarketing page");
        return pagePath + "simpleModelView";
    }

    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY)"
    )
    @GetMapping("/admin/delivery")
    public String adminDelivery(Model model) {
        model.addAttribute("result", "adminDelivery page");
        return pagePath + "simpleModelView";
    }

    @GetMapping("/system/anyone")
    public String systemAnyone(Model model) {
        model.addAttribute("result", "systemAnyone page");
        return pagePath + "simpleModelView";
    }

    //secure filter chain 에서는 제약이 없지만 컨트럴로에 Role 제약이 걸려 있는 상황에 대한 test 를 위해
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/public/anyone/butNeedRole")
    public String butNeedRole(Model model) {
        model.addAttribute("result", "butNeedRole page");
        return pagePath + "simpleModelView";
    }

    //secure filter chain 에서 특정 authority 제약이 걸린 케이스 테스트
    @GetMapping("/public/anyone/butNeedAuth")
    public String butNeedAuth(Model model) {
        model.addAttribute("result", "butNeedAuth page");
        return pagePath + "simpleModelView";
    }

    //secure filter chain 에서는 제약이 없지만 컨트럴로에 Authority 제약이 걸려 있는 상황에 대한 test 를 위해
    @GetMapping("/public/anyone/butNeedAuth2")
    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST)"
    )
    @Operation(summary = "security butNeedAuth2", description = "security butNeedAuth2 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> butNeedAuth2() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("butNeedAuth2"));
    }
}
