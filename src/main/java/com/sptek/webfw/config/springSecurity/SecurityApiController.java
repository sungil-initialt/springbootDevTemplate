package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.config.springSecurity.extras.dto.SigninRequestDto;
import com.sptek.webfw.example.dto.ValidationTestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = {"/api/v1/"})
@Tag(name = "security", description = "인증/인가 api 그룹")
public class SecurityApiController {
    @Autowired
    private SecurityService securityService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;


    //여기 봐야함
    @PostMapping("/login")
    public String getLoginToken(@RequestBody SigninRequestDto signinRequestDto) {
        //상황에 맞게 id, pw 요소 선정하여 UsernamePasswordAuthenticationToken 를 만들어냄 (여기서는 id를 email로 사용)
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(signinRequestDto.getEmail(), signinRequestDto.getPassword());
        log.debug("new authenticationToken: {}", authenticationToken);


        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER,"Bearer " + jwt);

        return jwt;
    }

}
