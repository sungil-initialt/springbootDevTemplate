package com.sptek.webfw.config.filter;

import com.sptek.webfw.util.ReqResUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*
MVC 인터셉터 방법보다 Fiter 방식이 추후 커스텀하기에 좋음
 */
@Slf4j
@Order(1)
//@WebFilter적용시 @Component 사용하지 않아야함(@Component 적용시 모든 요청에 적용됨)
//@Component
@WebFilter(urlPatterns = "/api/*") //ant 표현식 사용 불가 ex: /**
public class CorsPolicyFilter extends OncePerRequestFilter {

    @Value("${secureOption.cors.defaultAccessControlAllowOrigin}")
    private String defaultAccessControlAllowOrigin;
    @Value("#{'${secureOption.cors.accessControlAllowOrigin}'.split(',')}")
    private List<String> accessControlAllowOrigins;
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
        log.debug("[Filter >>> ]");
        String origin = Optional.ofNullable(ReqResUtil.getRequestHeaderMap(request).get("Origin"))
                .orElseGet(() -> ReqResUtil.getRequestHeaderMap(request).get("origin"));

        log.debug("request origin({}) contained ? : {}", origin, accessControlAllowOrigins.contains(origin));
        origin = accessControlAllowOrigins.contains(origin) ? origin : defaultAccessControlAllowOrigin;

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
}
