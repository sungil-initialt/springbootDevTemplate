package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@WebFilter(urlPatterns = "/*")
public class MethodeUsageAuditFilter extends OncePerRequestFilter {

    public MethodeUsageAuditFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull jakarta.servlet.FilterChain filterChain) throws ServletException, IOException {
//
//        // 요청 처리 전
//        LoggingAspect.clearCallStack();
//
//        // 체인 다음 필터로 요청 전달
//        filterChain.doFilter(request, response);
//
//        // 응답 처리 후
//        List<String> callStack = LoggingAspect.getCallStack();
//        StringBuilder filteredCallStack = new StringBuilder("Filtered Call Stack:\n");
//        callStack.forEach(methodName -> filteredCallStack.append(methodName).append("\n"));
//
//        // 로깅
//        log.debug(filteredCallStack.toString());
//
//        // Clear call stack after logging
//        LoggingAspect.clearCallStack();
    }
}