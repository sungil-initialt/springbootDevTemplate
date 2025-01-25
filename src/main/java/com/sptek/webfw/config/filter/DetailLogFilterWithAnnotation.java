package com.sptek.webfw.config.filter;

import com.sptek.webfw.annotation.EnableDetailLogFilter;
import com.sptek.webfw.base.constant.CommonConstants;
import com.sptek.webfw.base.constant.RequestMappingAnnotationRegister;
import com.sptek.webfw.util.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) //순서에 장단점이 있을 수 있음
@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.DetailLogFilterWithAnnotation", havingValue = "true", matchIfMissing = false)
public class DetailLogFilterWithAnnotation extends OncePerRequestFilter {
    // todo: 어노테이션 속성값을 통해 파일 저장하는 기능 추가 (속성값을 로그 맨 앞 프리픽스로 만들어야 함)

    public DetailLogFilterWithAnnotation() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        //request, response을 ContentCachingRequestWrapper, ContentCachingResponseWrapper 변환하여 하위 플로우로 넘긴다.(req, res 의 body를 여러번 읽기 위한 용도로 활용됨)

        if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()
                //@EnableDetailLogFilter 가 적용된 클레스 또는 메스드만 적용됨
                || !RequestMappingAnnotationRegister.hasAnnotation(request, EnableDetailLogFilter.class)
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // Request와 Response를 ContentCachingWrapper로 래핑
        ContentCachingRequestWrapper contentCachingRequestWrapper = request instanceof ContentCachingRequestWrapper ? (ContentCachingRequestWrapper)request : new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper;
        boolean amIContentCachingResponseWrapperOwner = false;
        if(response instanceof ContentCachingResponseWrapper) {
            contentCachingResponseWrapper = (ContentCachingResponseWrapper)response;
        } else {
            contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);
            amIContentCachingResponseWrapperOwner = true;
        }

        // 다른 필터나 컨트롤러 쪽에서 변형 될수 있음으로 필터 체인 전에 처리
        String session = contentCachingRequestWrapper.getSession().getId();
        String methodType = RequestUtil.getRequestMethodType(contentCachingRequestWrapper);
        String url = RequestUtil.getRequestUrlQuery(contentCachingRequestWrapper);
        String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(contentCachingRequestWrapper, "|"));
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(contentCachingRequestWrapper));

        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);

        // 컨트롤러 쪽에서 requestBody 먼저 한번 읽을수 있도록 필터 체인이 다시 돌아 왔을때 처리
        String requestBody = SptFwUtil.getRequestBody(contentCachingRequestWrapper);
        String responseHeader = TypeConvertUtil.strMapToString(ResponseUtil.getResponseHeaderMap(contentCachingResponseWrapper, "|"));

        if(request.getRequestURI().startsWith("/api/")) {
            String responseBody = SptFwUtil.getResponseBody(contentCachingResponseWrapper);
            String logBody = String.format(
                      "session : %s\n"
                    + "(%s) url : %s\n"
                    + "params : %s\n"
                    + "requestHeader : %s\n"
                    + "requestBody : %s\n"
                    + "responseHeader : %s\n"
                    + "responseBody(%s) : %s\n"
                    , session
                    , methodType, url
                    , params
                    , requestHeader
                    , StringUtils.hasText(requestBody)? "\n" + requestBody : ""
                    , responseHeader
                    , response.getStatus(), StringUtils.hasText(responseBody)? "\n" + responseBody : ""
            );

            //tagName 은 해당 로깅의 시작 키워드로 지정되며 로그 내용을 검색하기 위한 키워드 또는 파일로 저장하기 위한 기준으로 활용
            String tagName = RequestMappingAnnotationRegister.getAnnotationAttributes(request, EnableDetailLogFilter.class).get("value").toString();
            log.info(SptFwUtil.convertSystemNotice(tagName,"Request-Response Information caught by the DetailLogFilterWithAnnotation", logBody));

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
                    + "modelAndView(%s) : %s\n"
                    + "exceptionMsg : %s\n"
                    , session
                    , methodType, url
                    , params
                    , requestHeader
                    , StringUtils.hasText(requestBody)? "\n" + requestBody : ""
                    , responseHeader
                    , response.getStatus(), StringUtils.hasText(responseModelAndView)? "\n" + responseModelAndView : ""
                    , exceptionMsg
            );
            log.info(SptFwUtil.convertSystemNotice("Request-Response Information caught by the DetailLogFilterWithAnnotation", logBody));
        }

        // contentCachingResponseWrapper 을 자신이 직접 생성했다면 필터 체인 이후 response body 복사 (필수)
        if (amIContentCachingResponseWrapperOwner) {
            contentCachingResponseWrapper.copyBodyToResponse();
        }
    }

}
