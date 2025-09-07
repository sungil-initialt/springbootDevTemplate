package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore._annotation.Enable_NoFilterAndSessionForMinorRequest_At_Main;
import com.sptek._frameworkWebCore._annotation.Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import com.sptek._frameworkWebCore.util.Timer;
import jakarta.annotation.PostConstruct;
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
//@Profile(value = { "local", "dev", "stg", "prd" })
//@WebFilter(urlPatterns = "/*")
public class ReqResDetailLogFilter extends OncePerRequestFilter {
    // todo: 어노테이션 속성값을 통해 파일 저장하는 기능 추가 (속성값을 로그 맨 앞 프리픽스로 만들어야 함)

    @PostConstruct
    public void init() {
        //log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //request, response을 ContentCachingRequestWrapper, ContentCachingResponseWrapper 로 변환 하여 하위 플로우 로 넘긴다.(req, res 의 body를 여러번 읽기 위한 용도로 활용됨)

        // 필터 동작 여부 (제외 케이스)
        boolean hasNoDetailLogAnnotation = !MainClassAnnotationRegister.hasAnnotation(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class) && !RequestMappingAnnotationRegister.hasAnnotation(request, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class);
        boolean isMinorRequest = MainClassAnnotationRegister.hasAnnotation(Enable_NoFilterAndSessionForMinorRequest_At_Main.class) && (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest());
        if (hasNoDetailLogAnnotation || isMinorRequest) {
            filterChain.doFilter(request, response);
            return;
        }

        // 필터 적용 전 (ContentCachingWrapper 상태가 아니라면 래핑)
        var contentCachingRequestWrapper = request instanceof ContentCachingRequestWrapper ? (ContentCachingRequestWrapper)request : new ContentCachingRequestWrapper(request);
        var amIContentCachingResponseWrapperOwner = !(response instanceof ContentCachingResponseWrapper);
        var contentCachingResponseWrapper = amIContentCachingResponseWrapperOwner ? new ContentCachingResponseWrapper(response) : (ContentCachingResponseWrapper)response;

        // todo: 중요! Async 디스패치의 두번째 호출일때 새로 들어온 response 의 경우 copyBodyToResponse 가 동작하지 않는 현상이 있음, 해결 방안으로 첫번째 호출때의 response를 저장해서 사용함
        if (amIContentCachingResponseWrapperOwner && request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE) == null) {
            request.setAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE, response);
        }

        // todo: 중요! Async 디스패치의 두번째 호출일때 새로 들어온 response 의 경우 copyBodyToResponse 가 동작하지 않는 현상이 있음, 해결 방안으로 첫번째 호출때의 response를 저장해서 사용함
        if (amIContentCachingResponseWrapperOwner && isAsyncDispatch(request)) {
            contentCachingResponseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_KEEPING_ORIGIN_RESPONSE));
        }

        try {
            filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
            // isAsyncStarted(request) = Async 를 요청해놓고 돌아온 케이스 인지 (다시말해 !isAsyncStarted(request) = Async 가 아닌 그냥 일반 요청)
            // isAsyncDispatch(request) = Async 요청을 마무리하고 돌아온 케이스(두번째 호출) 인지
            // todo: 중요! Async 디스패치의 첫번째 호출일때는 처리 의미 없음
            if (!isAsyncStarted(request) || isAsyncDispatch(request)) {
                // req 매핑이 없는(404, 405) 케이스는 ApplicationGlobalExceptionHandler 에서 로깅 됨으로 제외 (todo : hasRequestMapping 이 리소스 낭비일까?)
                if (Timer.measure("hasRequestMapping", () -> RequestMappingAnnotationRegister.hasRequestMapping(request))) {
                    LoggingUtil.reqResDetailLogging(log, contentCachingRequestWrapper, contentCachingResponseWrapper, "Req Res Detail Log From " + this.getClass().getSimpleName());
                }
            } else {
                log.debug("First Async Dispatcher called.");
            }
        } finally {
            // todo: 중요! contentCachingResponseWrapper 을 자신이 직접 생성 했다면 필터 체인 이후 response body 복사 (필수)
            if ((!isAsyncStarted(request) || isAsyncDispatch(request)) && amIContentCachingResponseWrapperOwner) {
                contentCachingResponseWrapper.copyBodyToResponse();
            }
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
}