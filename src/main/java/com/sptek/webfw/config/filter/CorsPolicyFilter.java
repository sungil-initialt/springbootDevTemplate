package com.sptek.webfw.config.filter;

import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/*
MVC 인터셉터 방법보다 Fiter 방식이 추후 커스텀하기에 좋음
 */

//@WebFilter적용시 @Component 사용하지 않아야함(@Component 적용시 모든 요청에 적용됨)
//@Component
@Profile(value = { "stg", "prd" }) //프로파일이 stg, prd 일때만 적용
@Slf4j
@Order(1)
@WebFilter(urlPatterns = "/api/*") //ant 표현식 사용 불가 ex: /**
public class CorsPolicyFilter extends OncePerRequestFilter {
    final boolean IS_FILTER_ON = true;

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
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if(IS_FILTER_ON) {
            // todo: 필터 제외 케이스 를 적용하는게 맞을까? 보안 협의가 필요
            if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }

            log.info("#### Filter Notice : {} is On ####", this.getClass().getSimpleName());
            String origin = Optional.ofNullable(ReqResUtil.getRequestHeaderMap(request).get("Origin"))
                    .orElseGet(() -> ReqResUtil.getRequestHeaderMap(request).get("origin"));

            log.debug("The request origin is {}", origin);
            if(accessControlAllowOrigins.contains(origin)){
                log.debug("The request origin is part of our system");
            } else {
                log.debug("The request origin is not our system");
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

        } else {
            log.info("#### Filter Notice : {} is OFF ####", this.getClass().getSimpleName());
            filterChain.doFilter(request, response);
        }
    }
}
