package com.sptek._frameworkWebCore.config.springSecurity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandlerForView implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String exCode = "000";

        if (exception instanceof UsernameNotFoundException) {
            log.info("Bad credentials");
            exCode = "EX001";

        } else if (exception instanceof BadCredentialsException) {
            log.info("Username not found");
            exCode = "EX002";

        } else {
            log.info("Unknown exception");
            exCode = "EX000";
        }

        //todo : exception 케이스별로 메시지를 내릴수도 있지만 보안상의 이유로 안내려주는게 더 안전하니 적절히 판단 필요
        response.sendRedirect("/login?error=" + exCode);
    }
}