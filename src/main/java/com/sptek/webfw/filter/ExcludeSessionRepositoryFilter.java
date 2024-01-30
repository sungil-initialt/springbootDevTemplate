package com.sptek.webfw.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
org.springframework.session:spring-session-data-redis 을 사용하게 되면 SessionRepositoryFilter 가 자동 등록되게 되는데
이로인해 결과적으로 불필요한 session이 redis 등에 저장되는 경우가 발생한다. (기타 단순 resource 요청, 헬스체크등의 요청과 같은 경우)
이를 회피하기 위한 필터임.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) //최상위 필터로 적용
public class ExcludeSessionRepositoryFilter  extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws ServletException, IOException {
        if (isStaticResources(httpRequest) || isExcludePath(httpRequest)) {
            httpRequest.setAttribute("org.springframework.session.web.http.SessionRepositoryFilter.FILTERED", Boolean.TRUE);
        }
        filterChain.doFilter(httpRequest, httpResponse);
    }

    private boolean isStaticResources(HttpServletRequest request) {
        //제외하고 싶은 extention 추가하면 됨
        String url = request.getRequestURL().toString();
        String[] staticResourceExtentions = {".js", ".css", ".json", ".eot", ".otf", "woff", ".png", ".jpg", ".gif", ".ico"};

        for (String staticResourceExtention: staticResourceExtentions) {
            if (url.endsWith(staticResourceExtention))
                return true;
        }
        return false;
    }

    private boolean isExcludePath(HttpServletRequest request) {
        //제외하고 싶은 path 추가하면 됨
        String[] excludePathPatterns = {"/js", "/css", "/img", "/image", "/images", "/error", "/health"};
        String reqPath = request.getServletPath();

        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String excludePathPattern : excludePathPatterns) {
            if(pathMatcher.match(excludePathPattern, reqPath))
                return true;
        }
        return false;

    }
}
