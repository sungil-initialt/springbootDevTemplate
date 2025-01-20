package com.sptek.webfw.config.filter;

import com.sptek.webfw.base.constant.CommonConstants;
import com.sptek.webfw.support.HttpServletRequestWrapperSupport;
import com.sptek.webfw.support.HttpServletResponseWrapperSupport;
import com.sptek.webfw.util.RequestUtil;
import com.sptek.webfw.util.ResponseUtil;
import com.sptek.webfw.util.SecureUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/*
모든  request, response 에 대한 전체 로그를 남긴다. 성능 및 메모리 소묘가 큼으로 로컬에서 개발 디버깅용으로만 사용하여야 한다. 상용 적용 금지.
*/
@Profile(value = { "local" })
@Slf4j
@Order(3)
@WebFilter(urlPatterns = "/*") //ant 표현식 사용 불가 ex: /**
@ConditionalOnProperty(name = "sptFramework.filters.isEnabled.DEPRECATED_ReqResLogFilterForDebugging", havingValue = "true", matchIfMissing = false)
public class DEPRECATED_ReqResLogFilterForDebugging extends OncePerRequestFilter {

    public DEPRECATED_ReqResLogFilterForDebugging() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied.");
    }

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {

        //필터 제외 케이스
        if (SecureUtil.isNotEssentialRequest() || SecureUtil.isStaticResourceRequest()) {
            filterChain.doFilter(request, response);
            return;
        }

        HttpServletRequestWrapperSupport httpServletRequestWrapperSupport = request instanceof HttpServletRequestWrapperSupport ? (HttpServletRequestWrapperSupport)request : new HttpServletRequestWrapperSupport(request);
        HttpServletResponseWrapperSupport httpServletResponseWrapperSupport = response instanceof HttpServletResponseWrapperSupport ? (HttpServletResponseWrapperSupport)response : new HttpServletResponseWrapperSupport(response);

        String session = httpServletRequestWrapperSupport.getSession().getId();
        String methodType = RequestUtil.getRequestMethodType(httpServletRequestWrapperSupport);
        String url = RequestUtil.getRequestUrlQuery(httpServletRequestWrapperSupport);
        String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(httpServletRequestWrapperSupport, "|"));
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(httpServletRequestWrapperSupport));
        String requestBody = httpServletRequestWrapperSupport.getRequestBody(); // todo: 컨틀럴러에서 @RequestBody 로 읽은 후에는 가져올수 없기 때문에 필터 체인으로 넘기기 전에 미리 request body를 읽어 둠

        filterChain.doFilter(httpServletRequestWrapperSupport, httpServletResponseWrapperSupport);

        String responseHeader = TypeConvertUtil.strMapToString(ResponseUtil.getResponseHeaderMap(httpServletResponseWrapperSupport, "|"));

        if(request.getRequestURI().startsWith("/api/")) {
            String responseBody = httpServletResponseWrapperSupport.getResponseBody();
            log.debug("\n--------------------\n[ **** Request-Response Information caught by the ReqResLogFilterForDebugging **** ]\n--------------------\n" +
                            "session : {}\n" +
                            "({}) url : {}\n" +
                            "params : {}\n" +
                            "requestHeader : {}\n" +
                            "requestBody : {}\n" +
                            "responseHeader : {}\n" +
                            "responseBody({}) : {}\n" +
                            "--------------------\n"
                    , session
                    , methodType, url
                    , params
                    , requestHeader
                    , StringUtils.hasText(requestBody)? "\n" + requestBody : ""
                    , responseHeader
                    , response.getStatus(), StringUtils.hasText(responseBody)? "\n" + responseBody : ""
            );

        } else {
            String exceptionMsg = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE)).map(Object::toString).orElse("No Exception");
            String responseModelAndView = Optional.ofNullable(request.getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_MODELANDVIEW)).map(Object::toString).orElse("");
            log.debug("\n--------------------\n[ **** Request-Response Information caught by the ReqResLogFilterForDebugging **** ]\n--------------------\n" +
                            "session : {}\n" +
                            "({}) url : {}\n" +
                            "params : {}\n" +
                            "requestHeader : {}\n" +
                            "requestBody : {}\n" +
                            "responseHeader : {}\n" +
                            "modelAndView({}) : {}\n" +
                            "exceptionMsg : {}\n" +
                            "--------------------\n"
                    , session
                    , methodType, url
                    , params
                    , requestHeader
                    , StringUtils.hasText(requestBody)? "\n" + requestBody : ""
                    , responseHeader
                    , response.getStatus(), StringUtils.hasText(responseModelAndView)? "\n" + responseModelAndView : ""
                    , exceptionMsg
            );
        }

        // todo: 중요!! 자신이 response를 HttpServletResponseWrapperSupport로 변환한 최초의 필터라면 response에 body를 최종 write 할 책음을 져야 한다.
        //  (httpServletResponseWrapperSupport가 아닌 response 객체에 써야함)
        if (!(response instanceof HttpServletResponseWrapperSupport)) {
            response.getWriter().write(httpServletResponseWrapperSupport.getResponseBody());
        }
    }
}


