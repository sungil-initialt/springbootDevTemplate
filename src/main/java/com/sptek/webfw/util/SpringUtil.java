package com.sptek.webfw.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SpringUtil {
    @Getter
    private static Environment environment;


    @Autowired
    SpringUtil(Environment environment) {
        SpringUtil.environment = environment;
    }

    public static String getProperty(String key) {
        // 프로퍼티 조회 순서 : jvm 시스템 속상 > OS 환경 변수 > 프로퍼티 파일
        return SpringUtil.environment.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return SpringUtil.environment.getProperty(key, defaultValue);
    }

    //request에서 도메인 정보를 가져옴
    public static String getRequestDomain() {
        HttpServletRequest request = getRequest();
        String domain = request.getScheme() + "://" + request.getServerName();

        if (request.getServerPort() != 80 && request.getServerPort() != 443) {
            domain += ":" + request.getServerPort();
        }

        return domain;
    }

    //request에서 쿼리를 포함한 전체 uri를 가져옴
    public static String getRequestUrlQuery() {
        HttpServletRequest request = getRequest();

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString != null) {
            urlBuilder.append("?").append(queryString);
        }
        return urlBuilder.toString();
    }

    //request에서 요청 메소드 가져옴
    public static String getRequestMethodType() {
        HttpServletRequest request = getRequest();
        return request.getMethod();
    }

    //request에서 모든 해더 정보를 추출해 Map으로 반환
    public static Map<String, String> getRequestHeaderMap() {
        HttpServletRequest request = getRequest();

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
    public static Map<String, String[]> getRequestParameterMap() {
        HttpServletRequest request = getRequest();
        return request.getParameterMap();
    }

    //request에서 클라이언트의 최종 IP를 추출함
    public static String getReqUserIp() {
        HttpServletRequest request = getRequest();
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

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }

    public static HttpSession getSession(boolean create) {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(create);
    }

    public static Object setSessionAttribute(String attributeName, Object attribute) {
        getSession(true).setAttribute(attributeName, attribute);
        return attribute;
    }

    public static Object getSessionAttribute(String attributeName) {
        return getSession(true).getAttribute(attributeName);
    }

    public static Map<String, Object> getSessionAttributesAll(boolean create) {
        HttpSession session = SpringUtil.getSession(create);
        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            attributes.put(attributeName, session.getAttribute(attributeName));
        }

        return attributes;
    }

}

