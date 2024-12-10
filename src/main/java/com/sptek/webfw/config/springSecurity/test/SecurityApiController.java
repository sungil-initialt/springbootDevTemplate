package com.sptek.webfw.config.springSecurity.test;

import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.config.springSecurity.CustomJwtFilter;
import com.sptek.webfw.config.springSecurity.GeneralTokenProvider;
import com.sptek.webfw.config.springSecurity.extras.dto.LoginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = {"/api/v1/"})
@Tag(name = "security", description = "인증/인가 api 그룹")
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

    @GetMapping("/auth/hello")
    @Operation(summary = "auth hello", description = "auth hello 테스트", tags = {"echo"}) //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> authHello(
            @Parameter(name = "message", description = "ehco 로 응답할 내용", required = true) //swagger
            @RequestParam String message) {

        return ResponseEntity.ok(new ApiSuccessResponseDto<>(message));
    }

    @GetMapping("/public/hello")
    @Operation(summary = "public hello", description = "public hello 테스트", tags = {"echo"}) //swagger
    public ResponseEntity<ApiSuccessResponseDto<String>> publicHello(
            @Parameter(name = "message", description = "ehco 로 응답할 내용", required = true) //swagger
            @RequestParam String message) {

        return ResponseEntity.ok(new ApiSuccessResponseDto<>(message));
    }

}