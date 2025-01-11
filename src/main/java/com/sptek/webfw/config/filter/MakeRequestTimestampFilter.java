package com.sptek.webfw.config.filter;

import com.sptek.webfw.common.constant.CommonConstants;
import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

//@Profile(value = { "xxx" })
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //최상위 필터로 적용 (최대한 실제 요청에 가깝게 timestamp를 만들기 위해)
@WebFilter(urlPatterns = "/api/*") //ant 표현식 사용 불가 ex: /**
public class MakeRequestTimestampFilter extends OncePerRequestFilter {
    private final boolean IS_FILTER_ON;

    public MakeRequestTimestampFilter(@Value("${filters.isEnabled.MakeRequestTimestampFilter}") Boolean isFilterOn) {
        IS_FILTER_ON = isFilterOn;
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if(IS_FILTER_ON) {
            //필터 제외 케이스
            if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }

            request.setAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_TIMESTAMP, LocalDateTime.now());
            filterChain.doFilter(request, response);

        }else{
            filterChain.doFilter(request, response);
        }
    }
}
