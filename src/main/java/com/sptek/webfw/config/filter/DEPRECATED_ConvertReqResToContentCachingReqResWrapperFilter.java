package com.sptek.webfw.config.filter;

import com.sptek.webfw.base.constant.CommonConstants;
import com.sptek.webfw.util.SecureUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.io.IOException;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE) //response의 가장 최종 정보를 얻기 위해 가능하면 가장 순위가 낮은 필터에 적용함
@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.DEPRECATED_ConvertReqResToContentCachingReqResWrapperFilter", havingValue = "true", matchIfMissing = false)
public class DEPRECATED_ConvertReqResToContentCachingReqResWrapperFilter extends OncePerRequestFilter {

    public DEPRECATED_ConvertReqResToContentCachingReqResWrapperFilter() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //request, response을 ContentCachingRequestWrapper, ContentCachingResponseWrapper 변환하여 하위 플로우로 넘긴다.(req, res 의 body를 읽기 위한 용도로 ReqResLoggingInterceptor 에서 활용됨)

        //필터 제외 케이스
        if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper contentCachingRequestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
        contentCachingResponseWrapper.copyBodyToResponse();
    }
}
