package com.sptek._frameworkWebCore._example.unit.security;

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
@Tag(name = "security", description = "")


public class SecurityApiController {
    private final AuthenticationManager authenticationManager;
    private final GeneralTokenProvider generalTokenProvider;

    // 권한 오픈 경로
    @GetMapping("/public/authFree")
    @Operation(description = "권한에 제한을 받지 않은 경로") //swagger
    public Object notAuthHello() {
        return "welcome to authFree path.";
    }

    // 보안상 반드시 Post 로 구성할 것
    @PostMapping("/public/login")
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
