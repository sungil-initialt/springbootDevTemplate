package com.sptek.webfw.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RequestUtil {

    //request에서 도메인 정보를 가져옴
    public static String getRequestDomain(@NotNull HttpServletRequest request) {
        String domain = request.getScheme() + "://" + request.getServerName();

        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            domain += ":" + request.getServerPort();
        }

        return domain;
    }

    //request에서 쿼리를 포함한 전체 uri를 가져옴
    public static @NotNull String getRequestUrlQuery(@NotNull HttpServletRequest request) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString != null) {
            urlBuilder.append("?").append(queryString);
        }
        return urlBuilder.toString();
    }

    //request에서 요청 메소드 가져옴
    public static String getRequestMethodType(@NotNull HttpServletRequest request) {
        return request.getMethod();
    }

    //request에서 모든 해더 정보를 추출해 Map으로 반환
    public static @NotNull Map<String, String> getRequestHeaderMap(@NotNull HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    //request에서 모든 param을 추출해 Map으로 반환
    public static Map<String, String[]> getRequestParameterMap(@NotNull HttpServletRequest request) {
        return request.getParameterMap();
    }

    //request에서 클라이언트의 최종 IP를 추출함
    public static String getReqUserIp(@NotNull HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (!StringUtils.hasText(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.length() > 23) { // IPv6
            ip = ip.substring(0, 23);
        }

        log.debug("final requester ip : {}", ip);
        return ip;
    }

    public static Object getSessionAttribute(@NotNull HttpServletRequest request, String attributeName) {
        return request.getSession(true).getAttribute(attributeName);
    }

    public static @NotNull Map<String, Object> getSessionAttributesAll(@NotNull HttpServletRequest request, boolean create) {
        HttpSession session = request.getSession(create);
        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            attributes.put(attributeName, session.getAttribute(attributeName));
        }

        return attributes;
    }

}

