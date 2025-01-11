package com.sptek.webfw.util;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    public static HashMap<String, String> getRequestHeaderMap(HttpServletRequest request){
        return getRequestHeaderMap(request, "");
    }

    public static HashMap<String, String> getRequestHeaderMap(HttpServletRequest request, String delimiter) {
        StringBuilder headerString = new StringBuilder();
        HashMap<String, String> headers = new HashMap<>();

        // 요청 헤더 이름을 가져오기
        Set<String> headerNames = TypeConvertUtil.enumerationToSet(request.getHeaderNames());
        // 모든 헤더를 순회하며 로그로 남기기
        for (String headerName : headerNames) {
            Enumeration<String> headerValues = request.getHeaders(headerName);

            // 헤더 값을 리스트 형태로 변환하여 출력
            StringBuilder values = new StringBuilder();
            while (headerValues.hasMoreElements()) {
                values.append(headerValues.nextElement()).append(", ");
            }

            // 마지막 쉼표와 공백 제거
            if (values.length() > 0) {
                values.setLength(values.length() - 2);  // 마지막 쉼표와 공백 제거
            }

            // 최종 문자열에 추가
            //headerString.append(headerName).append(" = ").append(values.toString()).append("\n");
            headers.put(headerName, values.append(delimiter).toString());
        }
        return headers;
    }

}

