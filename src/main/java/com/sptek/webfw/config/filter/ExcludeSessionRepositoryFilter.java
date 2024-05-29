package com.sptek.webfw.config.filter;

import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
org.springframework.session:spring-session-data-redis 을 사용하게 되면 SessionRepositoryFilter 가 자동 등록되게 되는데
이로인해 결과적으로 불필요한 session이 redis 등에 저장되는 경우가 발생한다. (기타 단순 resource 요청, 헬스체크등의 요청과 같은 경우)
이를 회피하기 위한 필터임.
todo : redis 연동후 실제 동작 확인 필요!!
 */

@Profile(value = { "notused" })
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //최상위 필터로 적용
//@WebFilter적용시 @Component 사용하지 않아야함(@Component 적용시 모든 요청에 적용됨)
//@Component
@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
public class ExcludeSessionRepositoryFilter  extends OncePerRequestFilter {

    @Override
     public void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws ServletException, IOException {
        log.debug("[Filter >>> ]");
        if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
            //해당 이름의 Attribute가 True이면 spring은 해당 request에 대한 세션 생성을 하지 않는다.
            httpRequest.setAttribute("org.springframework.session.web.http.SessionRepositoryFilter.FILTERED", Boolean.TRUE);
            log.debug("setAttribute for ExcludeSessionRepository of {}", httpRequest.getServletPath());
        }

        filterChain.doFilter(httpRequest, httpResponse);
    }
}
