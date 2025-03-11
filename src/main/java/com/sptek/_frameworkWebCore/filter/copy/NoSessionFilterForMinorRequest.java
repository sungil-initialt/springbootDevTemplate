package com.sptek._frameworkWebCore.filter.copy;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


//@Component //@WebFilter적용시 @Component 사용하지 않아야함(@Component 적용시 모든 요청에 적용됨)
@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE) //최대한 높은 순위로 지정해야 함
//@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.ExcludeSessionRepositoryFilter", havingValue = "true", matchIfMissing = false)
//@HasAnnotationOnMain_InBean(EnableNoSessionFilterForMinorRequest.class)
//@WebFilter(urlPatterns = "/*")
public class NoSessionFilterForMinorRequest extends OncePerRequestFilter {
    /*
    org.springframework.session:spring-session-data-redis 을 사용하게 되면 SessionRepositoryFilter 가 자동 등록 되게 되는데
    이는 http 기본 세션 관리 방식이 아닌 SessionRepositoryFilter 를 통해(redis 같은) 처리 하게 되는 의미임
    이때 동일한 요청에 대해 여러번 SessionRepositoryFilter 가 동작 하는 경우가 있을 수 있는데 그런 경우 redis 서버 등에 부하를 줄수 있어
    spring 내부 적으로 한 번 요청된 경우는 request attribute 에 "org.springframework.session.web.http.SessionRepositoryFilter.FILTERED" 값을 true로
    설정하여 중복 작업을 방지하는 매커니즘이 있음. 이 메커니즘을 이용해서 불필요한 request(static 파일 요청등) 에 대해서는 애초 부터 해당 값을 true로
    설정하여 redis 요청을 한번도 하지 않게 처리하는 용도로 만든 필터임
    todo : redis 연동후 실제 동작 확인 필요!!
     */

    public NoSessionFilterForMinorRequest() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
     public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
            //중요 요청이 아닌 경우 이미 세션 처리를 끝낸것 처럼 강제 세팅함
            request.setAttribute("org.springframework.session.web.http.SessionRepositoryFilter.FILTERED", Boolean.TRUE);
            log.debug("Not Essential Request: {}", request.getServletPath());
        }

        filterChain.doFilter(request, response);
    }
}
