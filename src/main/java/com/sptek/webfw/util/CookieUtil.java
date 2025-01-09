package com.sptek.webfw.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/*
CookieUtil 을 사용하지 않고 ResponseCookie 객체를 직접 builder 방식으로 사용하는게 가장 좋음.(익숙하지 않은사람을 위해 남김)
*/

@Slf4j
public class CookieUtil {

    private static final String DEFAULT_COOKIE_PATH = "/";

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

    public static @NotNull Cookie createCookie(@NotNull String name, @NotNull String value, @NotNull Integer maxAgeSec) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAgeSec);
        return cookie;
    }

    public static @NotNull Cookie createCookie(@NotNull String name, @NotNull String value, @NotNull Integer maxAge, @NotNull boolean isHttpOnly, @NotNull boolean secure, String domain, String path) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);

        cookie.setHttpOnly(isHttpOnly);
        cookie.setSecure(secure);

        cookie.setDomain(StringUtils.hasText(domain) ? domain : "");
        cookie.setPath(StringUtils.hasText(path) ? path : DEFAULT_COOKIE_PATH);

        return cookie;
    }


    public static void addCookie(@NotNull Cookie cookie) {
        addCookie(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse(), cookie);
    }
    public static void addCookie(@NotNull HttpServletResponse response, @NotNull Cookie cookie) {
        response.addCookie(cookie);
        log.debug("addCookie : {}", cookie);
    }


    public static void addResponseCookie(@NotNull ResponseCookie responseCookie) {
       addResponseCookie(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse(), responseCookie);
    }
    public static void addResponseCookie(@NotNull HttpServletResponse response, @NotNull ResponseCookie responseCookie) {
        response.addHeader("Set-Cookie", responseCookie.toString());
        log.debug("addResponseCookie : {}", responseCookie);
    }


    public static ArrayList<Cookie> getCookies(@NotNull String name) {
        return getCookies(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest(), name);
    }
    public static ArrayList<Cookie> getCookies(@NotNull HttpServletRequest request, @NotNull String name) {
        ArrayList<Cookie> resultCookies = Optional.ofNullable(request.getCookies())
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(cookie -> name.equals(cookie.getName()))
                .collect(Collectors.toCollection(ArrayList::new));

        return resultCookies;
    }


    public static void deleteCookie(@NotNull String name) {
        deleteCookie(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse(), name);
    }
    public static void deleteCookie(@NotNull HttpServletResponse response, @NotNull String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
    }


    public static void deleteCookies(@NotNull String... cookieNames) {
        deleteCookies(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse(), cookieNames);
    }
    public static void deleteCookies(@NotNull HttpServletResponse response, @NotNull String... cookieNames) {
        for(String cookieName : cookieNames){
            deleteCookie(response, cookieName);
        }
    }
}