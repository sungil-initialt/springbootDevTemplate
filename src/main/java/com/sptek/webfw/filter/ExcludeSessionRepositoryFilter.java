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

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExcludeSessionRepositoryFilter  extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain filterChain) throws ServletException, IOException {
        if (isStaticResources(httpRequest) || isExcludePath(httpRequest)) {
            httpRequest.setAttribute("org.springframework.session.web.http.SessionRepositoryFilter.FILTERED", Boolean.TRUE);
        }
        filterChain.doFilter(httpRequest, httpResponse);
    }

    private boolean isStaticResources(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        String[] staticResourceExtentions = {".js", ".css", ".json", ".eot", ".otf", "woff", ".png", ".jpg", ".gif", ".ico"};

        for (String staticResourceExtention: staticResourceExtentions) {
            if (url.endsWith(staticResourceExtention))
                return true;
        }
        return false;
    }

    private boolean isExcludePath(HttpServletRequest request) {
        String[] excludePathPatterns = {"/error", "/health-chck"};
        String reqPath = request.getServletPath();

        AntPathMatcher pathMatcher = new AntPathMatcher();
        for (String excludePathPattern : excludePathPatterns) {
            if(pathMatcher.match(excludePathPattern, reqPath))
                return true;
        }
        return false;

    }
}
