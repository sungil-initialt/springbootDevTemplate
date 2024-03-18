package com.sptek.webfw.config.filter;

import com.sptek.webfw.util.ReqResUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Order(1)
public class CorsPolicyWithFilter extends OncePerRequestFilter {

    @Value("${secureOption.cors.defaultAccessControlAllowOrigin}")
    private String defaultAccessControlAllowOrigin;
    @Value("#{'${secureOption.cors.accessControlAllowOrigin}'.split(',')}")
    private List<String> accessControlAllowOriginList;
    @Value("${secureOption.cors.accessControlAllowMethods}")
    private String accessControlAllowMethods;
    @Value("${secureOption.cors.accessControlAllowCredentials}")
    private String accessControlAllowCredentials;
    @Value("${secureOption.cors.accessControlMaxAge}")
    private String accessControlMaxAge;
    @Value("${secureOption.cors.accessControlAllowHeaders}")
    private String accessControlAllowHeaders;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("accessControlAllowOriginList : {}", accessControlAllowOriginList);
        //origin 해더가 없는 경우 조합해서 만듬
        String origin = Optional.ofNullable(ReqResUtil.getRequestHeaderMap(request).get("Origin"))
                .orElseGet(() -> request.getRequestURL().substring(0, request.getRequestURL().length() - request.getRequestURI().length() + request.getContextPath().length()));

        origin = accessControlAllowOriginList.contains(origin) ? origin : defaultAccessControlAllowOrigin;
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", accessControlAllowMethods);
        response.setHeader("Access-Control-Allow-Credentials", accessControlAllowCredentials);
        response.setHeader("Access-Control-Max-Age", accessControlMaxAge);
        response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
    //테스트 해봐야함
}
