package com.sptek._frameworkWebCore._example.unit.authentication;

import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.springSecurity.CustomJwtFilter;
import com.sptek._frameworkWebCore.springSecurity.GeneralTokenProvider;
import com.sptek._frameworkWebCore.springSecurity.extras.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController(value = "securityApiControllerTemp")
@EnableResponseOfApiCommonSuccess_InRestController
@EnableResponseOfApiGlobalException_InRestController
@RequestMapping(value = {"/api/v1/example/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "authentication", description = "")

public class AuthenticationApiController {

    private final AuthenticationManager authenticationManager;
    private final GeneralTokenProvider generalTokenProvider;
    private final AuthenticationService authenticationService;

    @GetMapping("/authentication/authFree")
    @Operation(summary = "1. 권한 제한이 없는 path", description = "") //swagger
    public Object authFree() {
        return "welcome to authFree path.";
    }

    @GetMapping("/secured-Any-Auth/authentication/needAuth")
    @Operation(summary = "2. Any Auth 가 필요함", description = "") //swagger
    public Object needAnyAuth() {
        return "you have Auth (Any)";
    }

    @GetMapping("/secured-Special-Auth/authentication/needAuth")
    @Operation(summary = "3. Special Auth 가 필요함", description = "") //swagger
    public Object needSpecialAuth() {
        return "you have Auth (Special)";
    }

    @GetMapping("/secured-User-Role/authentication/needAuth")
    @Operation(summary = "4. User Role 이 필요함", description = "") //swagger
    public Object needUserRole() {
        return "you have Role (User)";
    }

    @GetMapping("/secured-System-Role/authentication/needAuth")
    @Operation(summary = "5. System Role 이 필요함", description = "") //swagger
    public Object needSystemRole() {
        return "you have Role (System)";
    }

    @GetMapping("/secured-Admin-AdminSpecial-Role/authentication/needAuth")
    @Operation(summary = "6. Admin or AdminSpecial Role 이 필요함", description = "") //swagger
    public Object needAdminOrAdminSpecialRole() {
        return "you have Role (Admin or AdminSpecial)";
    }

    @RequestMapping(value = "/postSecured-Any-Auth/authentication/needAuthWhenPost", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "7. POST 요청은 Any Auth 가 필요함", description = "") //swagger
    public Object needAnyAuthWhenPost() {
        return "you need Auth (Any) when you request with POST";
    }

    @GetMapping("/authentication/authFreeWithAuthCheckMethod")
    @Operation(summary = "8. public 영역 이지만 내부 적으로 Auth 체크 메소드 를 사용 하는 경우", description = "") //swagger
    public Object authFreeWithAuthCheckMethod() {
        authenticationService.iNeedAut();
        return "welcome to authFree path.";
    }

    @GetMapping("/authentication/authFreeWithRoleCheckMethod")
    @Operation(summary = "9. public 영역 이지만 내부 적으로 Role 체크 메소드 를 사용 하는 경우", description = "") //swagger
    public Object authFreeWithRoleCheckMethod() {
        authenticationService.iNeedRole();
        return "welcome to authFree path.";
    }

    // 보안상 반드시 Post 로 구성할 것
    @PostMapping("/authentication/login")
    @Operation(summary = "10. 로그인 API (인증 토큰)", description = "") //swagger
    public Object signin(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        // RequestBody 에서 id, pw 항목을 선정하여 UsernamePasswordAuthenticationToken 를 만들어 낸후
        // authenticationManager의 절차를 통해 Authentication을 생성하고 SecurityContextHolder 에 직접 저장하고 (form UI 방식의 경우는 직접 저장하지 않음)
        // Authentication을 JWT로 변환하여 해더에 넣어주는 것 까지 처리함

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication); //다음 요청을 위한 것이 아니라 현재 요청 내 이코드 이후 코드 에서 사용될 여지가 있기에 저장함
        String jwt = generalTokenProvider.convertAuthenticationToJwt(authentication);

        //아래 방식으로 변경
        //HttpHeaders httpHeaders = new HttpHeaders();
        //httpHeaders.add(CustomJwtFilter.getAuthorizationHeader(), CustomJwtFilter.getAuthorizationPrefix() + jwt);
        //return ResponseEntity.ok().headers(httpHeaders).body(new ApiCommonSuccessResponseDto<>(jwt));

        response.setHeader(CustomJwtFilter.getAuthorizationHeader(), CustomJwtFilter.getAuthorizationPrefix() + jwt);
        return jwt;
    }

}
