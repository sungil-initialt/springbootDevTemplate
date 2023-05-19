package com.sptek.webfw.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

@Slf4j
public class CookieUtil {

    private static final String DEFAULT_COOKIE_PATH = "/";

    public static @NotNull Cookie createCookie(@NotNull String name, @NotNull String value) {
        Cookie cookie = new Cookie(name, value);
        return cookie;
    }
    public static @NotNull Cookie createCookie(@NotNull String name, @NotNull String value, @NotNull Integer maxAge, boolean isHttpOnly, boolean secure, String domain, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(isHttpOnly);
        cookie.setSecure(secure);

        if (!StringUtils.isEmpty(domain)) {
            cookie.setDomain(domain);
        }

        cookie.setPath(!StringUtils.isEmpty(path) ? path : DEFAULT_COOKIE_PATH);
        return cookie;
    }

    public static @NotNull ResponseCookie createResponseCookie(@NotNull String name, @NotNull String value, @NotNull Integer maxAge, boolean isHttpOnly, boolean secure, String domain, String path, String sameSite) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
            .maxAge(maxAge)
            .httpOnly(isHttpOnly)
            .secure(secure)
            .domain(org.springframework.util.StringUtils.hasText(domain) ? domain : "")
            .path(org.springframework.util.StringUtils.hasText(path) ? path : DEFAULT_COOKIE_PATH);

        if (StringUtils.hasText(sameSite)) {
            String formattedSameSite = sameSite.toLowerCase();

            if (formattedSameSite.equals("none") || formattedSameSite.equals("lax") || formattedSameSite.equals("strict")) {
                cookieBuilder.sameSite(formattedSameSite);
            } else {
                throw new IllegalArgumentException("Invalid SameSite value: " + sameSite);
            }
        }

        return cookieBuilder.build();
    }

    public static void addCookie(@NotNull HttpServletResponse response, @NotNull Cookie cookie) {
        response.addCookie(cookie);

        if (log.isDebugEnabled()) {
            log.debug("addCookie : {}", cookie.toString());
        }
    }

    public static void addResponseCookie(@NotNull HttpServletResponse response, @NotNull ResponseCookie responseCookie) {
        response.addHeader("Set-Cookie", responseCookie.toString());

        if (log.isDebugEnabled()) {
            log.debug("addResponseCookie : {}", responseCookie.toString());
        }
    }

    public static ArrayList<Cookie> getCookies(@NotNull HttpServletRequest request, @NotNull String name) {
        Cookie cookies[] = request.getCookies();
        ArrayList<Cookie> resultCookieList = new ArrayList<>();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    resultCookieList.add(cookie);
                }
            }
        }
        return resultCookieList;
    }

    public static void deleteCookie(@NotNull HttpServletResponse response, @NotNull String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
    }



    public class CookieUtilEx extends CookieUtil {
        public static void deleteCookies(@NotNull HttpServletResponse response, @NotNull String... cookieNames) {
            for(String cookieName : cookieNames){
                deleteCookie(response, cookieName);
            }
        }
    }
}
