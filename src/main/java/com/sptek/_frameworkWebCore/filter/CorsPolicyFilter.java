package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import com.sptek._frameworkWebCore.util.SptFwUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/*
MVC 인터셉터 방법보다 Fiter 방식이 추후 커스텀하기에 좋음
 */

//@WebFilter적용시 @Component 사용하지 않아야함(@Component 적용시 모든 요청에 적용됨)
//@Component
@Slf4j
@Profile(value = { "local", "dev", "stg", "prd" })
//@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.CorsPolicyFilter", havingValue = "true", matchIfMissing = false)
@Order(Ordered.LOWEST_PRECEDENCE)
@WebFilter(urlPatterns = "/api/*") //브라우저에서 실제 CORS 확인 처리는 api 호출때 요첨 됨으로.. //ant 표현식 사용 불가 ex: /**
//@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
public class CorsPolicyFilter extends OncePerRequestFilter {

    private String defaultAccessControlAllowOrigin;
    private List<String> accessControlAllowOrigins;
    private String accessControlAllowMethods;
    private String accessControlAllowCredentials;
    private String accessControlMaxAge;
    private String accessControlAllowHeaders;

    public CorsPolicyFilter(@Value("${cors.options.defaultAccessControlAllowOrigin}") String defaultAccessControlAllowOrigin,
                            @Value("#{'${cors.options.accessControlAllowOrigin}'.split(',')}") List<String> accessControlAllowOrigins,
                            @Value("${cors.options.accessControlAllowMethods}") String accessControlAllowMethods,
                            @Value("${cors.options.accessControlAllowCredentials}") String accessControlAllowCredentials,
                            @Value("${cors.options.accessControlMaxAge}") String accessControlMaxAge,
                            @Value("${cors.options.accessControlAllowHeaders}") String accessControlAllowHeaders
    ) {
        this.defaultAccessControlAllowOrigin = defaultAccessControlAllowOrigin;
        this.accessControlAllowOrigins = accessControlAllowOrigins;
        this.accessControlAllowMethods = accessControlAllowMethods;
        this.accessControlAllowCredentials = accessControlAllowCredentials;
        this.accessControlMaxAge = accessControlMaxAge;
        this.accessControlAllowHeaders = accessControlAllowHeaders;

        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
        log.info(SptFwUtil.convertSystemNotice("CORS Policy Properties"
                ,"defaultAccessControlAllowOrigin: " + defaultAccessControlAllowOrigin + "\n"
                        + "accessControlAllowOrigins: " + accessControlAllowOrigins +"\n"
                        + "accessControlAllowMethods: " + accessControlAllowMethods +"\n"
                        + "accessControlAllowCredentials: " + accessControlAllowCredentials +"\n"
                        + "accessControlMaxAge: " + accessControlMaxAge +"\n"
                        + "accessControlAllowHeaders: " + accessControlAllowHeaders
        ));
    }

//    @Override
//    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
//        // todo: 필터 제외 케이스 를 적용하는게 맞을까? 보안 협의가 필요
//        if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String origin = Collections.list(request.getHeaderNames()).stream()
//                .filter(headerName -> headerName.equalsIgnoreCase("Origin"))
//                .findFirst() // Origin 헤더의 첫번째 값을 사용함, 기본적으로 Origin은 하나만 있어야 함
//                .map(request::getHeader)
//                .orElse("[No Origin]");
//
//        if(accessControlAllowOrigins.contains(origin)){
//            log.debug("The request origin is part of our system. (origin: {})", origin);
//        } else {
//            log.warn("The request origin is not our system. (origin: {})", origin);
//        }
//
//        origin = accessControlAllowOrigins.contains(origin) ? origin : defaultAccessControlAllowOrigin;
//        response.setHeader("Access-Control-Allow-Origin", origin);
//        response.setHeader("Access-Control-Allow-Methods", accessControlAllowMethods);
//        response.setHeader("Access-Control-Allow-Credentials", accessControlAllowCredentials);
//        response.setHeader("Access-Control-Max-Age", accessControlMaxAge);
//        response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
//
//        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//            // todo : 필터체인 연결이 필요없나 확인 필요! (option은 실제 요청이 아니라 확인용임으로 특별한 후 처리가 필요해 보이지는 않음!)
//
//        } else {
//            filterChain.doFilter(request, response);
//        }
//    }

    //-->스프링 시큐리티가 먼저 체가는 느낌임..
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // todo: 필터 제외 케이스 를 적용하는게 맞을까? 보안 협의가 필요
        if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        String origin = Collections.list(request.getHeaderNames()).stream()
                .filter(headerName -> headerName.equalsIgnoreCase("Origin"))
                .findFirst() // Origin 헤더의 첫번째 값을 사용함, 기본적으로 Origin은 하나만 있어야 함
                .map(request::getHeader)
                .orElse("NoOrigin");

        log.debug("CORS req Orign: {}", origin);

        // OPTIONS 요청 이면서 Origin 값이 있는 경우만 처리 (CORS 관련 Option 요청에 대해서만 처리, 그냥 Option 요청에 대해선 spring 기본 처리로 대처함)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) && !origin.equalsIgnoreCase("NoOrigin")) {
            String allowOrigin = "*".equals(defaultAccessControlAllowOrigin) || accessControlAllowOrigins.contains(origin)
                    ? origin
                    : defaultAccessControlAllowOrigin;

            log.debug("CORS allowOrigin: {}", allowOrigin);

            response.setHeader("Access-Control-Allow-Origin", allowOrigin);
            response.setHeader("Access-Control-Allow-Methods", accessControlAllowMethods);
            response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);
            response.setHeader("Access-Control-Allow-Credentials", accessControlAllowCredentials);
            response.setHeader("Access-Control-Max-Age", accessControlMaxAge);

            log.debug(origin.equals(allowOrigin)? "CORS policy validation passed." : "CORS policy validation denied.");
            response.setStatus(HttpServletResponse.SC_OK);

        } else {
            log.debug("CORS : it's not CORS check request.");
            filterChain.doFilter(request, response); // 다른 요청은 그대로 통과
        }
    }
}
