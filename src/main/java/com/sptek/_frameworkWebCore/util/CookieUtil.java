package com.sptek._frameworkWebCore.util;

import jakarta.servlet.http.Cookie;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
/*
CookieUtil 을 사용하지 않고 ResponseCookie 객체를 직접 builder 방식으로 사용하는게 가장 좋음.(익숙하지 않은사람을 위해 남김)
*/

@Slf4j
public class CookieUtil {
    private static final String DEFAULT_COOKIE_PATH = "/";


    public static void createResponseCookieAndAdd(@NotNull String name, @NotNull String value, @NotNull Integer maxAge, boolean isHttpOnly, boolean secure, String domain, String path, String sameSite) {
        addResponseCookie(createResponseCookie(name, value, maxAge, isHttpOnly, secure, domain, path, sameSite));
    }

    public static @NotNull ResponseCookie createResponseCookie(@NotNull String name, @NotNull String value, @NotNull Integer maxAge, boolean isHttpOnly, boolean secure, String domain, String path, String sameSite) {
        ResponseCookie.ResponseCookieBuilder cookieBuilder = ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .httpOnly(isHttpOnly)
                .secure(secure)
                .domain(StringUtils.hasText(domain) ? domain : "")
                .path(StringUtils.hasText(path) ? path : DEFAULT_COOKIE_PATH);

        Optional.ofNullable(sameSite)
                .map(String::toLowerCase)
                .filter(s -> Set.of("none", "lax", "strict").contains(s))
                .ifPresentOrElse(
                        cookieBuilder::sameSite,
                        () -> { throw new IllegalArgumentException("Invalid SameSite value: " + sameSite); }
                );

        return cookieBuilder.build();
    }

    public static void addResponseCookie(@NotNull ResponseCookie responseCookie) {
        SpringUtil.getResponse().addHeader("Set-Cookie", responseCookie.toString());
        log.debug("addResponseCookie : {}", responseCookie);
    }



    public static void createCookieAndAdd(@NotNull String name, @NotNull String value, @NotNull Integer maxAgeSec) {
        addCookie(createCookie(name, value, maxAgeSec));
    }

    public static @NotNull Cookie createCookie(@NotNull String name, @NotNull String value, @NotNull Integer maxAgeSec) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeSec);
        return cookie;
    }

    public static void createCookieAndAdd(@NotNull String name, @NotNull String value, @NotNull Integer maxAge, @NotNull boolean isHttpOnly, @NotNull boolean secure, String domain, String path) {
        addCookie(createCookie(name, value, maxAge, isHttpOnly, secure, domain, path));
    }

    public static @NotNull Cookie createCookie(@NotNull String name, @NotNull String value, @NotNull Integer maxAge, @NotNull boolean isHttpOnly, @NotNull boolean secure, String domain, String path) {
        Cookie cookie = createCookie(name, value, maxAge);

        cookie.setHttpOnly(isHttpOnly);
        cookie.setSecure(secure);

        cookie.setDomain(StringUtils.hasText(domain) ? domain : "");
        cookie.setPath(StringUtils.hasText(path) ? path : DEFAULT_COOKIE_PATH);

        return cookie;
    }

    public static void addCookie(@NotNull Cookie cookie) {
        SpringUtil.getResponse().addCookie(cookie);
        log.debug("addCookie : {}", cookie);
    }



    public static @NotNull ArrayList<Cookie> getCookies(@NotNull String name) {
        Cookie[] cookies = SpringUtil.getRequest().getCookies();
        return cookies == null ? new ArrayList<>() :
                Arrays.stream(cookies)
                        .filter(cookie -> name.equals(cookie.getName()))
                        .collect(Collectors.toCollection(ArrayList::new));
    }

    public static void deleteCookies(@NotNull String... cookieNames) {
        for(String cookieName : cookieNames){
            deleteCookie(cookieName);
        }
    }

    public static void deleteCookie(@NotNull String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        SpringUtil.getResponse().addCookie(cookie);
    }

}