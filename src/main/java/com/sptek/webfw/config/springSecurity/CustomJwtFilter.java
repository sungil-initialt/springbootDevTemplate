package com.sptek.webfw.config.springSecurity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
public class CustomJwtFilter extends GenericFilterBean {
    public static String AUTHORIZATION_HEADER = "Authorization";
    public static String JWT_TYPE = "Bearer ";
    private final GeneralTokenProvider generalTokenProvider;

    public CustomJwtFilter(GeneralTokenProvider generalTokenProvider){
        this.generalTokenProvider = generalTokenProvider;
    }

    // 해더에 jwt가 있는 경우 jwt를 authentication으로 변환하여 SecurityContextHolder에 저장하는 역할을 해야함
    // 비인증 상태로 JWT가 없거나 유효하지 않은경우 SecurityContext에 저장되는게 없음
    // 적접 에러 처리를 하지 않고 다른 필터에 의해 인증 필요 페이지로 갔는데 SecurityContext에 authentication이 없는 경우 그때 EX가 발생함
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestUri = httpServletRequest.getRequestURI();
        String jwt = getJwtFromRequest(httpServletRequest);

        // 해더에 JWT가 정상적으로 있는경우 authentication을 만들어 SecurityContext에 저장
        if(StringUtils.hasText(jwt) && generalTokenProvider.validateJwt(jwt)){
            Authentication authentication = generalTokenProvider.convertJwtToAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Authentication({}) has been saved in SecurityContextHolder, uri: {}", authentication.getName(), requestUri);

        } else {
            //jwt가 없거나 유효하지 않다도 직접 별다른 처리를 하지 않음(다른 필터에 맡김)
            log.debug("fail to convert jwt({}) to authentication, uri: {}", jwt, requestUri);
        }

        chain.doFilter(httpServletRequest,response);
    }

    // Request Header에서 토큰 정보를 꺼내오기
    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(JWT_TYPE)){
            log.debug("Bearer : " + bearerToken);
            return bearerToken.substring(JWT_TYPE.length());
        }
        return null;
    }
}