package com.sptek._frameworkWebCore.config.springSecurity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomJwtAuthenticationEntryPointForApi implements AuthenticationEntryPoint {
    //CustomErrorController 를 이용해서 Controller 외부 에러(필터쪽이나.. 기타 등등) 상황에 대한 처리를 하고 있어서 사용할 필요가 없음

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error(authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
