package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.config.springSecurity.extras.dto.SigninRequestDto;
import com.sptek.webfw.config.springSecurity.spt.CustomJwtFilter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private AuthenticationManager authenticationManager;

    //API 방식의 인증 요청
    @PostMapping("/signin")
    public ResponseEntity<ApiSuccessResponseDto<String>> signin(@RequestBody SigninRequestDto signinRequestDto) {
        // RequestBody 에서 id, pw 항목을 선정하여 UsernamePasswordAuthenticationToken 를 만들어 낸후
        // authenticationManager의 절차를 통해 Authentication을 생성하고 SecurityContextHolder 에 저장하고
        // Authentication을 JWT로 변환하여 해더에 더해주는것 까지 처리함

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(signinRequestDto.getUserName(), signinRequestDto.getPassword());

        // usernamePasswordAuthenticationToken 이 만들어 지면 Spring Security f/w의 절차대로 authenticationManager에 인증요청을 하면된다.
        //결과 처리 (api jwt 방식이기 때문에 SecurityContextHolder에 저장할 필요가 없으면 하지 않아도 된다, 결과는 호출자 필요에 따라 처리하면 된다.)
        //form 방식의 경우는 방방식을 처리해 주는 자체 필터 usernamePasswordAuthenticationFilter 내부에서 SecurityContextHolder에 저장해 줄것으로 예상함

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = generalTokenProvider.convertAuthenticationToJwt(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CustomJwtFilter.AUTHORIZATION_HEADER, CustomJwtFilter.JWT_TPYE + jwt);

        return ResponseEntity.ok().headers(httpHeaders).body(new ApiSuccessResponseDto(jwt));
    }

}
