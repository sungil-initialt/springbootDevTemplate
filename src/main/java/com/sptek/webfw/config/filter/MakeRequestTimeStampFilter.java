package com.sptek.webfw.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Profile(value = { "xxx" })
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //최상위 필터로 적용 (최대한 실제 요청에 가깝게 timestamp를 만들기 위해)
@WebFilter(urlPatterns = "/api/*") //ant 표현식 사용 불가 ex: /**
public class MakeRequestTimeStampFilter extends OncePerRequestFilter {
    final boolean IS_FILTER_ON = false;
    private final String requestTimeStampAttributeName;

    public MakeRequestTimeStampFilter(@Value("${request.reserved.attribute.requestTimeStamp}") String requestTimeStampAttributeName) {
        this.requestTimeStampAttributeName = requestTimeStampAttributeName;
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if(IS_FILTER_ON) {
            log.info("#### Filter Notice : makeRequestTimeStampFilter is On ####");
            request.setAttribute(this.requestTimeStampAttributeName, Instant.now());
            filterChain.doFilter(request, response);

        }else{
            log.info("#### Filter Notice : makeRequestTimeStampFilter is OFF ####");
            filterChain.doFilter(request, response);
        }
    }
}
