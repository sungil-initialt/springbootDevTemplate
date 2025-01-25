package com.sptek.webfw.config.springSecurity.test;

import com.sptek.webfw.annotation.EnableApiCommonSuccessResponse;
import com.sptek.webfw.annotation.EnableApiCommonErrorResponse;
import com.sptek.webfw.base.apiResponseDto.ApiCommonSuccessResponseDto;
import com.sptek.webfw.config.springSecurity.CustomJwtFilter;
import com.sptek.webfw.config.springSecurity.GeneralTokenProvider;
import com.sptek.webfw.config.springSecurity.extras.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = {"/api/v1/"})
@Tag(name = "security", description = "인증/인가 api 그룹")
@RestController
@EnableApiCommonSuccessResponse
@EnableApiCommonErrorResponse
public class SecurityApiController {
    private final SecurityService securityService;
    private final GeneralTokenProvider generalTokenProvider;
    private final AuthenticationManager authenticationManager;

    //API 방식의 인증 요청
    @PostMapping("/login")
    public Object signin(@RequestBody @Valid LoginRequestDto loginRequestDto, HttpServletResponse response) {
        // RequestBody 에서 id, pw 항목을 선정하여 UsernamePasswordAuthenticationToken 를 만들어 낸후
        // authenticationManager의 절차를 통해 Authentication을 생성하고 SecurityContextHolder 에 직접 저장하고 (form UI 방식의 경우는 직접 저장하지 않음)
        // Authentication을 JWT로 변환하여 해더에 넣어주는 것 까지 처리함

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(), loginRequestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = generalTokenProvider.convertAuthenticationToJwt(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.getAuthorizationHeader(), CustomJwtFilter.getAuthorizationPrefix() + jwt);
        return ResponseEntity.ok().headers(httpHeaders).body(new ApiCommonSuccessResponseDto<>(jwt));

        //response.setHeader(CustomJwtFilter.getAuthorizationHeader(), CustomJwtFilter.getAuthorizationPrefix() + jwt);
        //return jwt;
    }

    @GetMapping("/public/hello")
    @Operation(summary = "security notAuthHello", description = "security notAuthHello 테스트") //swagger
    public Object notAuthHello() {
        return "publicHello";
    }

    @GetMapping("/auth/hello")
    @Operation(summary = "security authHello", description = "security authHello 테스트") //swagger
    public Object authHello() {
        return "authHello";
    }

    @GetMapping("/user/hello")
    @Operation(summary = "security user", description = "security user 테스트") //swagger
    public Object userHello() {
        return "userHello";
    }

    @GetMapping("/admin/anyone")
    @Operation(summary = "security adminAnyone", description = "security adminAnyone 테스트") //swagger
    public Object adminAnyone() {
        return "adminAnyone";
    }

    @GetMapping("/admin/marketing")
    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_RETRIEVE_USER_ALL_FOR_MARKETING)"
    )
    @Operation(summary = "security adminMarketing", description = "security adminMarketing 테스트") //swagger
    public Object adminMarketing() {
        return "adminMarketing";
    }

    @GetMapping("/admin/delivery")
    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY)"
    )
    @Operation(summary = "security adminDelivery", description = "security adminDelivery 테스트") //swagger
    public Object adminDelivery() {
        return "adminDelivery";
    }

    @GetMapping("/system/anyone")
    @Operation(summary = "security systemAnyone", description = "security systemAnyone 테스트") //swagger
    public Object systemAnyone() {
        return "systemAnyone";
    }

    //secure filter chain 에서는 제약이 없지만 컨트럴로에 Role 제약이 걸려 있는 상황에 대한 test 를 위해
    @GetMapping("/public/anyone/butNeedControllRole")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "security butNeedControllRole", description = "security butNeedControllRole 테스트") //swagger
    public Object butNeedControllRole() {
        return "butNeedControllRole";
    }

    //secure filter chain 에서 특정 authority 제약이 걸린 케이스 테스트
    @GetMapping("/public/anyone/butNeedFilterAuth")
    @Operation(summary = "security butNeedFilterAuth", description = "security butNeedFilterAuth 테스트") //swagger
    public Object butNeedFilterAuth() {
        return "butNeedFilterAuth";
    }

    //secure filter chain 에서는 제약이 없지만 컨트럴로에 Authority 제약이 걸려 있는 상황에 대한 test 를 위해
    @GetMapping("/public/anyone/butNeedControllAuth")
    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST)"
    )
    @Operation(summary = "security butNeedControllAuth", description = "security butNeedControllAuth 테스트") //swagger
    public Object butNeedControllAuth() {
        return "butNeedControllAuth";
    }

    @RequestMapping({"/public/postPutAfterAuth"})
    public Object  postPutAfterAuth(Model model) {
        return "postPutAfterAuth";
    }
}