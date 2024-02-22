package com.sptek.webfw.interceptor;

import com.sptek.webfw.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/*
UV 관련 처리를 위한 인터셉터
 */
@Component
@Slf4j
public class UvInterceptor implements HandlerInterceptor {
    private final String uvCheckCookieName = "uvCheck";
    private final String uvCheckCookieValue = "checked";
    private final int uvCheckCookieMaxAgeSec = 60 * 60; //해당 시간동안 request 가 없었던 경우 새로운 방문으로 본다.

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String checkMsg = Optional.ofNullable(CookieUtil.getCookies(uvCheckCookieName))
                .filter(cookies -> !cookies.isEmpty())
                .map(cookies -> "update expire time")
                .orElse("created new");

        log.info("UvInterceptor : {}", checkMsg);

        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(uvCheckCookieName, uvCheckCookieValue)
                .maxAge(uvCheckCookieMaxAgeSec);
        CookieUtil.addResponseCookie(cookieBuilder.build());

        try {
            //처리하고 싶은 내옹 추가(selectOneTest()은 예시)
            log.debug("UvInterceptor : do more what you want");

        }catch (Exception ex){
            //중요 오류가 아니기 때문에 ex를 throw 하지 않는다.
            log.error("UvInterceptor : ", ex);
        }
        return true;
    }
}

