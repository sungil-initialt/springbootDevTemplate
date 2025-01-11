package com.sptek.webfw.config.filter;

import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Profile(value = { "local", "dev", "stg" }) //todo : 상용적용은 고민해 볼것(성능?)
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
public class MakeMdcFilter extends OncePerRequestFilter {
    private final boolean IS_FILTER_ON;

    public MakeMdcFilter(@Value("${filters.isEnabled.MakeMdcFilter}") Boolean isFilterOn) {
        IS_FILTER_ON = isFilterOn;
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //Mapped Diagnostic Context 를 사용하여 Slf4j 의 로깅 패턴에 특정 정보를 포함 할수 있도록 한다.
        //todo : 성능적 측면에서 오버해드가 발생할 수 있음으로 상용 적용시 고려 필요

        if(IS_FILTER_ON) {
            //필터 제외 케이스
            if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                // 세션 ID를 가져와 MDC에 추가
                MDC.put("sessionId", request.getSession(true).getId());
                MDC.put("memberId", SecurityContextHolder.getContext().getAuthentication().getName()); // todo: 멤버 계정을 보여주는 부분은 해당 시스템 별로 변경이 필요할수 있음, 또한 상용 적용시 보안이슈 체크 필요
                filterChain.doFilter(request, response);
            } finally {
                // 요청이 끝난 뒤 반드시 MDC 정리
                MDC.clear();
            }

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
