package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.config.springSecurity.extras.dto.SigninRequestDto;
import com.sptek.webfw.config.springSecurity.spt.CustomJwtFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = {"/api/v1/"})
@Tag(name = "security", description = "인증/인가 api 그룹")
public class SecurityApiController {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private GeneralTokenProvider generalTokenProvider;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    //API 방식의 인증 요청
    @PostMapping("/signin")
    public ResponseEntity<ApiSuccessResponseDto<String>> signin(@RequestBody SigninRequestDto signinRequestDto) {
        // RequestBody 에서 id, pw 항목을 선정하여 UsernamePasswordAuthenticationToken 를 만들어 낸후
        // authenticationManager의 절차를 통해 Authentication을 생성하고 SecurityContextHolder 에 저장하고
        // Authentication을 JWT로 변환하여 해더에 더해주는것 까치 처리함

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signinRequestDto.getEmail(), signinRequestDto.getPassword());
        log.debug("new authenticationToken: {}", authenticationToken);

        // authenticationToken이 만들어 지면 Spring Security f/w의 authenticationManager 의 동장 방식을 그데로 따르면 된다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = generalTokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, CustomJwtFilter.JWT_TPYE + jwt);

        return ResponseEntity.ok().headers(httpHeaders).body(new ApiSuccessResponseDto(jwt));
    }

}
