package com.sptek._frameworkWebCore.filter;

import com.sptek._frameworkWebCore.annotation.Enable_DetailLog_At_Main_Controller_ControllerMethod;
import com.sptek._frameworkWebCore.annotation.Enable_NoFilterAndSessionForMinorRequest_At_Main;
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
import java.util.Optional;

@Slf4j
//@Profile(value = { "local", "dev", "stg", "prd" })
//@WebFilter(urlPatterns = "/*")
public class DetailLogFilter extends OncePerRequestFilter {
    // todo: 어노테이션 속성값을 통해 파일 저장하는 기능 추가 (속성값을 로그 맨 앞 프리픽스로 만들어야 함)

    @PostConstruct //Bean 생성 이후 호출
    public void init() {
        //log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //request, response을 ContentCachingRequestWrapper, ContentCachingResponseWrapper 로 변환 하여 하위 플로우 로 넘긴다.(req, res 의 body를 여러번 읽기 위한 용도로 활용됨)
        //log.debug("DetailLogFilterWithAnnotation start");

        // 필터 제외 케이스
        boolean isMinorRequest = MainClassAnnotationRegister.hasAnnotation(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)
                && (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest());
        boolean hasNoDetailLogAnnotation = !MainClassAnnotationRegister.hasAnnotation(Enable_DetailLog_At_Main_Controller_ControllerMethod.class)
                && !RequestMappingAnnotationRegister.hasAnnotation(request, Enable_DetailLog_At_Main_Controller_ControllerMethod.class);

        if (isMinorRequest || hasNoDetailLogAnnotation) {
            filterChain.doFilter(request, response);
            return;
        }

        // 필터 적용 케이스 (Request와 Response를 ContentCachingWrapper로 래핑)
        ContentCachingRequestWrapper contentCachingRequestWrapper = request instanceof ContentCachingRequestWrapper ? (ContentCachingRequestWrapper)request : new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper;
        boolean amIContentCachingResponseWrapperOwner = false;

        if(response instanceof ContentCachingResponseWrapper) {
            contentCachingResponseWrapper = (ContentCachingResponseWrapper)response;
        } else {
            contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);
            amIContentCachingResponseWrapperOwner = true;
        }

        // 다른 필터나 controller 쪽에서 변형 될수 있음 으로 필터 체인 전에 처리
        String sessionId = contentCachingRequestWrapper.getSession().getId();
        String methodType = RequestUtil.getRequestMethodType(contentCachingRequestWrapper);
        String url = RequestUtil.getRequestUrlQuery(contentCachingRequestWrapper);
        String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(contentCachingRequestWrapper, "|"));
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(contentCachingRequestWrapper));

        // 컨트롤러에서 body 를 먼저 읽을 수 있도록 여기서 필터를 넘기고 body는 체인이 돌아 왔을때 처리 함
        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);
        String requestBody = StringUtils.hasText(RequestUtil.getRequestBody(contentCachingRequestWrapper)) ? "\n" + RequestUtil.getRequestBody(contentCachingRequestWrapper) : "";

        String logFileName = String.valueOf(RequestMappingAnnotationRegister.getAnnotationAttributes(request, Enable_DetailLog_At_Main_Controller_ControllerMethod.class).get("value"));
        String relatedOutbounds = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_RELATED_OUTBOUNDS)).map(Object::toString).orElse("");
        String responseHeader = TypeConvertUtil.strMapToString(ResponseUtil.getResponseHeaderMap(contentCachingResponseWrapper, "|"));

        if((request.getRequestURI().startsWith("/api/") || request.getRequestURI().startsWith("/systemSupportApi/")) && !request.getRequestURI().contains("/notEssential/") ) {
            String responseBody = StringUtils.hasText(ResponseUtil.getResponseBody(contentCachingResponseWrapper)) ? "\n" + ResponseUtil.getResponseBody(contentCachingResponseWrapper) : "";
            String logContent = """
                    sessionId: %s
                    (%s) url: %s
                    params: %s
                    requestHeader: %s
                    requestBody: %s
                    responseHeader: %s
                    relatedOutbounds: %s
                    responseBody(%s): %s
                    """.formatted(sessionId, methodType, url, params, requestHeader, requestBody, responseHeader, relatedOutbounds, response.getStatus(), responseBody);
            // todo: Annotation 이 main 과 controller 에 각각 적용 되어 있을때 controller 쪽의 tag 값으로 처리 되야 하는데 현재 그렇게 동작 하는지 확인 필요
            log.info(LoggingUtil.makeFwLogForm("REQ RES SUCCESS Detail Log caught by the DetailLogFilter", logContent, logFileName));

        } else {
            String exceptionMsg = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE)).map(Object::toString).orElse("No Exception");
            String responseModelAndView = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_MODEL_AND_VIEW)).map(Object::toString).orElse("");

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
                    modelAndView(%s): %s
                    exceptionMsg: %s
                    """.formatted(sessionId, methodType, url, params, requestHeader, StringUtils.hasText(requestBody)? "\n" + requestBody : "", responseHeader , relatedOutbounds
                            , RequestUtil.traceRequestDuration().getStartTime(), RequestUtil.traceRequestDuration().getCurrentTime(), RequestUtil.traceRequestDuration().getDurationMsec()
                            , response.getStatus(), StringUtils.hasText(responseModelAndView)? "\n" + responseModelAndView : "", exceptionMsg);
            log.info(LoggingUtil.makeFwLogForm("REQ RES SUCCESS Detail Log caught by the DetailLogFilter", logContent, logFileName));
        }

        // todo: 중요! contentCachingResponseWrapper 을 자신이 직접 생성 했다면 필터 체인 이후 response body 복사 (필수)
        if (amIContentCachingResponseWrapperOwner) {
            contentCachingResponseWrapper.copyBodyToResponse();
        }
    }
}
