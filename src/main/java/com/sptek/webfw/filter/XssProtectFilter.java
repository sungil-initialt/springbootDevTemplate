package com.sptek.webfw.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sptek.webfw.support.MyHttpServletRequestWrapper;
import com.sptek.webfw.util.SecureUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(1)
public class XssProtectFilter extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        MyHttpServletRequestWrapper wrappedRequest = new MyHttpServletRequestWrapper(request);
        String reqBody = IOUtils.toString(wrappedRequest.getReader());

        if (!StringUtils.isEmpty(reqBody)) {
            Map<String, Object> orgJsonObject = new ObjectMapper().readValue(reqBody, HashMap.class);
            Map<String, Object> newJsonObject = new HashMap<>();
            orgJsonObject.forEach((key, value) -> newJsonObject.put(key, SecureUtil.charEscape(value.toString())));
            wrappedRequest.resetInputStream(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(newJsonObject).getBytes());
        }

        filterChain.doFilter(wrappedRequest, response);
    }

}


