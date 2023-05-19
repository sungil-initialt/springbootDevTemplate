package com.sptek.webfw.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class ReqResUtil {

    public static String getRequestUrlString(HttpServletRequest request) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append("request_url=").append(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString != null) {
            urlBuilder.append("?").append(queryString);
        }
        return urlBuilder.toString();
    }

    public static Map<String, String> getRequestHeaderMap(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    public static Map<String, String[]> getRequestParameterMap(HttpServletRequest request) {
        return request.getParameterMap();
    }

    public static String getReqUserIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (StringUtils.isEmpty(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.length() > 23) { // IPv6
            ip = ip.substring(0, 23);
        }

        log.debug("============= CLIENT IP : [" + ip + "] =============");
        return ip;
    }
}

