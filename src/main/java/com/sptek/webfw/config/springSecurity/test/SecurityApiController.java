package com.sptek.webfw.config.springSecurity.test;

import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.config.springSecurity.CustomJwtFilter;
import com.sptek.webfw.config.springSecurity.GeneralTokenProvider;
import com.sptek.webfw.config.springSecurity.extras.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = {"/api/v1/"})
@Tag(name = "security", description = "인증/인가 api 그룹")
@RestController
public class SecurityApiController {
    private final SecurityService securityService;
    private final GeneralTokenProvider generalTokenProvider;
    private final AuthenticationManager authenticationManager;

    //API 방식의 인증 요청
    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponseDto<String>> signin(@RequestBody LoginRequestDto loginRequestDto) {
        // RequestBody 에서 id, pw 항목을 선정하여 UsernamePasswordAuthenticationToken 를 만들어 낸후
        // authenticationManager의 절차를 통해 Authentication을 생성하고 SecurityContextHolder 에 직접 저장하고 (form UI 방식의 경우는 직접 저장하지 않음)
        // Authentication을 JWT로 변환하여 해더에 넣어주는 것 까지 처리함

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getUserName(), loginRequestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = generalTokenProvider.convertAuthenticationToJwt(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, CustomJwtFilter.AUTHORIZATION_PREFIX + jwt);

        return ResponseEntity.ok().headers(httpHeaders).body(new ApiSuccessResponseDto<>(jwt));
    }

    @GetMapping("/public/hello")
    @Operation(summary = "security notAuthHello", description = "security notAuthHello 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> notAuthHello() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("notAuthHello"));
    }

    @GetMapping("/auth/hello")
    @Operation(summary = "security authHello", description = "security authHello 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> authHello() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("authHello"));
    }

    @GetMapping("/admin/anyone")
    @Operation(summary = "security adminAnyone", description = "security adminAnyone 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> adminAnyone() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("adminAnyone"));
    }

    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_RETRIEVE_USER_ALL_FOR_MARKETING)"
    )
    @GetMapping("/admin/marketing")
    @Operation(summary = "security adminMarketing", description = "security adminMarketing 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> adminMarketing() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("adminMarketing"));
    }

    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY)"
    )
    @GetMapping("/admin/delivery")
    @Operation(summary = "security adminDelivery", description = "security adminDelivery 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> adminDelivery() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("adminDelivery"));
    }

    @GetMapping("/system/anyone")
    @Operation(summary = "security systemAnyone", description = "security systemAnyone 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> systemAnyone() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("systemAnyone"));
    }

    //secure filter chain 에서는 제약이 없지만 컨트럴로에 Role 제약이 걸려 있는 상황에 대한 test 를 위해
    @GetMapping("/public/anyone/butNeedRole")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "security butNeedRole", description = "security butNeedRole 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> butNeedRole() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("butNeedRole"));
    }

    //secure filter chain 에서는 제약이 없지만 컨트럴로에 Authority 제약이 걸려 있는 상황에 대한 test 를 위해
    @GetMapping("/public/anyone/butNeedAuth")
    @PreAuthorize(
            "hasAuthority(T(com.sptek.webfw.config.springSecurity.AuthorityIfEnum).AUTH_SPECIAL_FOR_TEST)"
    )
    @Operation(summary = "security butNeedAuth", description = "security butNeedAuth 테스트") //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> butNeedAuth() {
        return ResponseEntity.ok(new ApiSuccessResponseDto<>("butNeedAuth"));
    }
}