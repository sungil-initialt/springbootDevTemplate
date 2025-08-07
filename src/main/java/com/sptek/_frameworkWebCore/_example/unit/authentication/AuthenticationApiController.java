package com.sptek._frameworkWebCore._example.unit.authentication;

import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController;
import com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiGlobalException_At_RestController;
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
@RestController
@Enable_ResponseOfApiCommonSuccess_At_RestController
@Enable_ResponseOfApiGlobalException_At_RestController
@RequestMapping(value = {"/api/"}, produces = {MediaType.APPLICATION_JSON_VALUE/*, MediaType.APPLICATION_XML_VALUE*/})
@Tag(name = "Authentication", description = "")

public class AuthenticationApiController {

    private final AuthenticationManager authenticationManager;
    private final GeneralTokenProvider generalTokenProvider;
    private final AuthenticationService authenticationService;

    @GetMapping("/01/example/authentication/authFree")
    @Operation(summary = "01. 권한 제한이 없는 path", description = "") //swagger
    public Object authFree() {
        return "Anyone can access.";
    }

    @GetMapping("/02/example/login/authentication/needAuth")
    @Operation(summary = "02. 로그인 필요함", description = "") //swagger
    public Object needAnyAuth() {
        return "you are logged in";
    }

    @GetMapping("/03/example/auth-special/authentication/needAuth")
    @Operation(summary = "03. Special Auth 가 필요함", description = "") //swagger
    public Object needSpecialAuth() {
        return "you have Auth (Special)";
    }

    @GetMapping("/04/example/role-user/authentication/needAuth")
    @Operation(summary = "04. User Role 이 필요함", description = "") //swagger
    public Object needUserRole() {
        return "you have Role (User)";
    }

    @GetMapping("/05/example/role-system/authentication/needAuth")
    @Operation(summary = "05. System Role 이 필요함", description = "") //swagger
    public Object needSystemRole() {
        return "you have Role (System)";
    }

    @GetMapping("/06/example/role-admin-adminSpecial/authentication/needAuth")
    @Operation(summary = "06. Admin or AdminSpecial Role 이 필요함", description = "") //swagger
    public Object needAdminOrAdminSpecialRole() {
        return "you have Role (Admin or AdminSpecial)";
    }

    @RequestMapping(value = "/07/example/postLogin/authentication/needAuth", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "07. POST 요청은 로그인 필요함", description = "") //swagger
    public Object needAnyAuthWhenPost() {
        return "you need Auth (Any) when you request with POST";
    }

    @GetMapping("/08/example/authentication/authFreeWithAuthCheckMethod")
    @Operation(summary = "08. Auth 경로가 아니지만 내부 적으로 Auth 체크 메소드 를 사용 하는 경우", description = "") //swagger
    public Object authFreeWithAuthCheckMethod() {
        return authenticationService.iNeedAuth();
    }

    @GetMapping("/09/example/authentication/authFreeWithRoleCheckMethod")
    @Operation(summary = "09. Auth 경로가 아니지만 내부 적으로 Role 체크 메소드 를 사용 하는 경우", description = "") //swagger
    public Object authFreeWithRoleCheckMethod() {
        return authenticationService.iNeedRole();
    }

    // 보안상 반드시 Post 로 구성할 것
    @PostMapping("/10/example/authentication/login")
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
