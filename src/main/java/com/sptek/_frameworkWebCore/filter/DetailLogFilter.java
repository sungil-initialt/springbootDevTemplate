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
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //log.debug("DetailLogFilterWithAnnotation start");
        //request, response을 ContentCachingRequestWrapper, ContentCachingResponseWrapper 로 변환 하여 하위 플로우 로 넘긴다.(req, res 의 bod y를 여러번 읽기 위한 용도로 활용됨)

        // pass 케이스
        if (MainClassAnnotationRegister.hasAnnotation(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)) {
            if (SecurityUtil.isNotEssentialRequest() || SecurityUtil.isStaticResourceRequest()) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // pass 케이스 (EnableDetailLog_InMain_Controller_ControllerMethod 가 없는 경우 그냥 페스함)
        if (!MainClassAnnotationRegister.hasAnnotation(Enable_DetailLog_At_Main_Controller_ControllerMethod.class)
                && !RequestMappingAnnotationRegister.hasAnnotation(request, Enable_DetailLog_At_Main_Controller_ControllerMethod.class)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 실제 필터 내용 (Request와 Response를 ContentCachingWrapper로 래핑)
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
        String session = contentCachingRequestWrapper.getSession().getId();
        String methodType = RequestUtil.getRequestMethodType(contentCachingRequestWrapper);
        String url = RequestUtil.getRequestUrlQuery(contentCachingRequestWrapper);
        String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(contentCachingRequestWrapper, "|"));
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(contentCachingRequestWrapper));

        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);

        // 컨트롤러 쪽에서 requestBody 먼저 한번 읽을수 있도록 필터 체인이 다시 돌아 왔을때 처리
        String requestBody = SptFwUtil.getRequestBody(contentCachingRequestWrapper);

        String tagName = String.valueOf(RequestMappingAnnotationRegister.getAnnotationAttributes(request, Enable_DetailLog_At_Main_Controller_ControllerMethod.class).get("value"));
        String relatedOutbounds = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_RELATED_OUTBOUNDS)).map(Object::toString).orElse("");
        // responseHeader 의 항목중 browser 가 생산 하는 내용도 많이 있음(서버의 로그와 browser 내용이 일치 않지 않는게 정상임)
        String responseHeader = TypeConvertUtil.strMapToString(ResponseUtil.getResponseHeaderMap(contentCachingResponseWrapper, "|"));


        String OUTBOUND_LOG_TEMPLATE = """
        session : %s
        (%s) url : %s
        params : %s
        requestHeader : %s
        requestBody : %s
        responseHeader : %s
        relatedOutbounds : %s
        responseBody(%s) : %s
        """;

        if((request.getRequestURI().startsWith("/api/") || request.getRequestURI().startsWith("/systemSupportApi/")) && !request.getRequestURI().contains("/notEssential/") ) {
            String responseBody = SptFwUtil.getResponseBody(contentCachingResponseWrapper);
            String logBody = OUTBOUND_LOG_TEMPLATE.formatted(
                    session
                    , methodType, url
                    , params
                    , requestHeader
                    , StringUtils.hasText(requestBody)? "\n" + requestBody : ""
                    , responseHeader
                    , relatedOutbounds
                    , response.getStatus(), StringUtils.hasText(responseBody)? responseBody : ""
            );

            // todo: Annotation 이 main 과 controller 에 각각 적용 되어 있을때 controller 쪽의 tag 값으로 처리 되야 하는데 현재 그렇게 동작 하는지 확인 필요
            log.info(SptFwUtil.convertSystemNotice(
                    StringUtils.hasText(tagName) && !tagName.equals("null") ? tagName : "--"
                    ,"Request-Response Information caught by the DetailLogFilterWithAnnotation"
                    , logBody));

        } else {
            String exceptionMsg = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE)).map(Object::toString).orElse("No Exception");
            String responseModelAndView = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_MODELANDVIEW)).map(Object::toString).orElse("");

            String logBody = String.format(
                      "session : %s\n"
                    + "(%s) url : %s\n"
                    + "params : %s\n"
                    + "requestHeader : %s\n"
                    + "requestBody : %s\n"
                    + "responseHeader : %s\n"
                    + "relatedOutbounds : %s\n"
                    + "requestTime : %s\n"
                    + "responseTime : %s\n"
                    + "durationMsec : %s\n"
                    + "modelAndView(%s) : %s\n"
                    + "exceptionMsg : %s\n"
                    , session
                    , methodType, url
                    , params
                    , requestHeader
                    , StringUtils.hasText(requestBody)? "\n" + requestBody : ""
                    , responseHeader
                    , relatedOutbounds
                    , RequestUtil.traceRequestDuration().getStartTime()
                    , RequestUtil.traceRequestDuration().getCurrentTime()
                    , RequestUtil.traceRequestDuration().getDurationMsec()
                    , response.getStatus(), StringUtils.hasText(responseModelAndView)? "\n" + responseModelAndView : ""
                    , exceptionMsg
            );
            log.info(SptFwUtil.convertSystemNotice(StringUtils.hasText(tagName) && !tagName.equals("null") ? tagName : "--"
                    ,"Request-Response Information caught by the DetailLogFilterWithAnnotation"
                    , logBody));
        }

        // todo: 중요! contentCachingResponseWrapper 을 자신이 직접 생성 했다면 필터 체인 이후 response body 복사 (필수)
        if (amIContentCachingResponseWrapperOwner) {
            contentCachingResponseWrapper.copyBodyToResponse();
        }
    }
}
