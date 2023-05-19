package com.sptek.webfw.interceptor;

import com.sptek.webfw.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class UvLoggingInterceptor implements HandlerInterceptor {

    private final String uvCheckCookieName = "uvCheck";
    private final String uvCheckCookieValue = "checked";
    private final int uvCheckCookieMaxAge = 1000 * 60 * 24;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(CookieUtil.getCookies(request, uvCheckCookieName).size() == 0){
            Cookie uvCheckCookie = CookieUtil.createCookie(uvCheckCookieName, uvCheckCookieValue);
            uvCheckCookie.setMaxAge(uvCheckCookieMaxAge);
            CookieUtil.addCookie(response, uvCheckCookie);

            log.info("UvLogging : checked");
        }

        return true;
    }
}

