package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
public abstract class ContentCachingFilterExample extends OncePerRequestFilter {

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        // 필터 적용 전 (Request와 Response를 ContentCachingWrapper로 래핑)
        ContentCachingRequestWrapper contentCachingRequestWrapper;
        ContentCachingResponseWrapper contentCachingResponseWrapper;
        boolean amIContentCachingResponseWrapperOwner = false;

        if (request instanceof ContentCachingRequestWrapper) contentCachingRequestWrapper = (ContentCachingRequestWrapper)request;
        else contentCachingRequestWrapper = new ContentCachingRequestWrapper(request);

        if (response instanceof ContentCachingResponseWrapper) {
            contentCachingResponseWrapper = (ContentCachingResponseWrapper) response;
        } else {
            contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);
            amIContentCachingResponseWrapperOwner = true;

            // todo: 중요! Async 디스패치의 두번째 호출일때 새로 들어온 response 의 경우 copyBodyToResponse 가 동작하지 않는 현상이 있음, 해결 방안으로 첫번째 호출때의 response를 저장해서 사용함
            // 이 시점의 response 는 비어 있음으로 메모리 소비는 거의 없음
            if (request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE) == null) {
                request.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE, response);
                //log.debug("{}", SpringUtil.getInstanceMemoryInfo(response));
            }
        }

        if (amIContentCachingResponseWrapperOwner && isAsyncDispatch(request)) {
            // todo: 중요! Async 디스패치의 두번째 호출일때 새로 들어온 response 의 경우 copyBodyToResponse 가 동작하지 않는 현상이 있음, 해결 방안으로 첫번째 호출때의 response를 저장해서 사용함
            contentCachingResponseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE));
        }

        // do what you want.**********************
        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
        if (!isAsyncStarted(request) || isAsyncDispatch(request)) { // todo: 중요! Async 디스패치의 첫번째 호출을 제외
            // do what you want.**********************

            // todo: 중요! contentCachingResponseWrapper 을 자신이 직접 생성 했다면 필터 체인 이후 response body 복사 (필수)
            if (amIContentCachingResponseWrapperOwner) {
                contentCachingResponseWrapper.copyBodyToResponse();
            }
        } else {
            log.debug("First Async Dispatcher called.");
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
}
