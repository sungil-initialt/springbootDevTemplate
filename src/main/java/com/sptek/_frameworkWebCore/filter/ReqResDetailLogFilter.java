package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore._annotation.Enable_NoFilterAndSessionForMinorRequest_At_Main;
import com.sptek._frameworkWebCore._annotation.Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.util.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
//@Profile(value = { "local", "dev", "stg", "prd" })
//@WebFilter(urlPatterns = "/*")
public class ReqResDetailLogFilter extends OncePerRequestFilter {
    // todo: 어노테이션 속성값을 통해 파일 저장하는 기능 추가 (속성값을 로그 맨 앞 프리픽스로 만들어야 함)

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        //log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //request, response을 ContentCachingRequestWrapper, ContentCachingResponseWrapper 로 변환 하여 하위 플로우 로 넘긴다.(req, res 의 body를 여러번 읽기 위한 용도로 활용됨)

        // 필터 동작 여부 (제외 케이스)
        boolean isMinorRequest = MainClassAnnotationRegister.hasAnnotation(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)
                && (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest());
        boolean hasNoDetailLogAnnotation = !MainClassAnnotationRegister.hasAnnotation(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class)
                && !RequestMappingAnnotationRegister.hasAnnotation(request, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class);
        if (isMinorRequest || hasNoDetailLogAnnotation) {
            filterChain.doFilter(request, response);
            return;
        }

        // 필터 적용 전 (Request와 Response를 ContentCachingWrapper로 래핑)
        ContentCachingRequestWrapper contentCachingRequestWrapper;
        ContentCachingResponseWrapper contentCachingResponseWrapper;
        boolean amIContentCachingResponseWrapperOwner = false;

        if (request instanceof ContentCachingRequestWrapper) {
            contentCachingRequestWrapper = (ContentCachingRequestWrapper)request;
        } else {
            contentCachingRequestWrapper = new ContentCachingRequestWrapper(request);
        }

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

        // Wrapper 형태로 체인을 넘긴다.
        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
        if (!isAsyncStarted(request) || isAsyncDispatch(request)) {
            // todo: 중요! Async 디스패치의 첫번째 호출일때는 로깅하지 않음

            String sessionId = contentCachingRequestWrapper.getSession().getId();
            String methodType = RequestUtil.getRequestMethodType(contentCachingRequestWrapper);
            String url = RequestUtil.getRequestUrlQuery(contentCachingRequestWrapper);
            String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(contentCachingRequestWrapper, "|"));
            String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(contentCachingRequestWrapper));
            String requestBody = RequestUtil.getRequestBody(contentCachingRequestWrapper);
            requestBody = StringUtils.hasText(requestBody) ? "\n" + requestBody : "";
            String relatedOutbounds = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_RELATED_OUTBOUNDS)).map(Object::toString).orElse("");
            String responseHeader = TypeConvertUtil.strMapToString(ResponseUtil.getResponseHeaderMap(contentCachingResponseWrapper, "|"));
            String isAsyncDispatch = isAsyncDispatch(request) ? ", Async Response" : "";

            // main 과 controller 쪽 양쪽에 적용되어 있는 경우 controller 쪽 annotation 이 우선함 (controller 전체와 controller 메소드 양쪽에 적용되는 경우는 RequestMappingAnnotationRegister 가 메소드쪽 정보를 가지고 있음)
            String logTag = StringUtils.hasText(Objects.toString(RequestMappingAnnotationRegister.getAnnotationAttributes(request, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class).get("value"), ""))
                    ? Objects.toString(RequestMappingAnnotationRegister.getAnnotationAttributes(request, Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class).get("value"), "")
                    : Objects.toString(MainClassAnnotationRegister.getAnnotationAttributes(Enable_ReqResDetailLog_At_Main_Controller_ControllerMethod.class).get("value"), "");

            if ((request.getRequestURI().startsWith("/api/") || request.getRequestURI().startsWith("/systemSupportApi/")) && !request.getRequestURI().contains("/notEssential/")) {
                String responseBody = ResponseUtil.getResponseBody(contentCachingResponseWrapper);
                responseBody = StringUtils.hasText(responseBody) ? "\n" + responseBody : "";
                String logContent = """
                        sessionId: %s
                        (%s) url: %s
                        params: %s
                        requestHeader: %s
                        requestBody: %s
                        responseHeader: %s
                        relatedOutbounds: %s
                        responseBody(%s%s): %s
                        """.formatted(sessionId, methodType, url, params, requestHeader, requestBody, responseHeader, relatedOutbounds, response.getStatus(), isAsyncDispatch, responseBody);
                log.info(LoggingUtil.makeBaseForm(logTag, "Req Res Success Detail Log caught by the ReqResDetailLogFilter", logContent));

            } else {
                String exceptionMsg = Optional.ofNullable(contentCachingRequestWrapper.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_EXCEPTION_MESSAGE)).map(Object::toString).orElse("No Exception");
                String responseModelAndView = Optional.ofNullable(contentCachingRequestWrapper.getAttribute(CommonConstants.REQ_ATTRIBUTE_FOR_LOGGING_MODEL_AND_VIEW)).map(Object::toString).orElse("");
                String logContent = """
                        sessionId: %s
                        (%s) url: %s
                        params: %s
                        requestHeader: %s
                        requestBody: %s
                        responseHeader: %s
                        relatedOutbounds: %s
                        requestTime: %s
                        responseTime: %s
                        durationMsec: %s
                        modelAndView(%s%s): %s
                        exceptionMsg: %s
                        """.formatted(sessionId, methodType, url, params, requestHeader, StringUtils.hasText(requestBody) ? "\n" + requestBody : "", responseHeader, relatedOutbounds
                        , RequestUtil.traceRequestDuration().getStartTime(), RequestUtil.traceRequestDuration().getCurrentTime(), RequestUtil.traceRequestDuration().getDurationMsec()
                        , response.getStatus(), isAsyncDispatch, StringUtils.hasText(responseModelAndView) ? "\n" + responseModelAndView : "", exceptionMsg);
                log.info(LoggingUtil.makeBaseForm(logTag, "Req Res Success Detail Log caught by the ReqResDetailLogFilter", logContent));
            }

            // todo: 중요! contentCachingResponseWrapper 을 자신이 직접 생성 했다면 필터 체인 이후 response body 복사 (필수)
            if (amIContentCachingResponseWrapperOwner) {
                contentCachingResponseWrapper.copyBodyToResponse();
            }
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }
}