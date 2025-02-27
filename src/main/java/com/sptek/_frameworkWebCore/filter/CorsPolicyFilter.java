package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@Profile(value = { "stg", "prd" }) //프로파일이 stg, prd 일때만 적용
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(urlPatterns = "/api/*") //ant 표현식 사용 불가 ex: /**
@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.CorsPolicyFilter", havingValue = "true", matchIfMissing = false)
public class CorsPolicyFilter extends OncePerRequestFilter {

    @Value("${cors.options.defaultAccessControlAllowOrigin}")
    private String defaultAccessControlAllowOrigin;
    @Value("#{'${cors.options.accessControlAllowOrigin}'.split(',')}")
    private List<String> accessControlAllowOrigins;
    @Value("${cors.options.accessControlAllowMethods}")
    private String accessControlAllowMethods;
    @Value("${cors.options.accessControlAllowCredentials}")
    private String accessControlAllowCredentials;
    @Value("${cors.options.accessControlMaxAge}")
    private String accessControlMaxAge;
    @Value("${cors.options.accessControlAllowHeaders}")
    private String accessControlAllowHeaders;

    public CorsPolicyFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

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
                .orElse("[No Origin]");

        if(accessControlAllowOrigins.contains(origin)){
            log.debug("The request origin is part of our system. (origin: {})", origin);
        } else {
            log.debug("The request origin is not our system. (origin: {})", origin);
        }

        origin = accessControlAllowOrigins.contains(origin) ? origin : defaultAccessControlAllowOrigin;
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", accessControlAllowMethods);
        response.setHeader("Access-Control-Allow-Credentials", accessControlAllowCredentials);
        response.setHeader("Access-Control-Max-Age", accessControlMaxAge);
        response.setHeader("Access-Control-Allow-Headers", accessControlAllowHeaders);

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            // todo : 필터체인 연결이 필요없나 확인 필요! (option은 실제 요청이 아니라 확인용임으로 특별한 후 처리가 필요해 보이지는 않음!)

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
