package com.sptek.webfw.config.interceptor;

import com.sptek.webfw.util.SpringUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
public class ReqResLoggingInterceptor implements HandlerInterceptor {
    // !!! 해당 Interceptor 를 사용하기 위해서는 ConvertReqResToContentCachingReqResWrapperFilter 가 적용되어 있어야 함
    // !!! Interceptor 특성상 컨트롤러 진입 이전의 에러에 대해서는 적용할 방법이 없다 (secure Filter 와 관련한 401, 403 에러의 경우가 그 예로, 필터 단계에서 처리하고 스스로 응답을 끝내기 때문이다.)

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        //log.debug("preHandle called");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable ModelAndView modelAndView) {
        //log.debug("postHandle called");
        request.setAttribute(SpringUtil.getProperty("request.reserved.attribute.modelAndViewForLogging", "MODEL_AND_VIEW_FOR_LOGGING"), modelAndView != null ? modelAndView.getModel() : Collections.emptyMap());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable Exception ex) {
        //최대한 에러 로깅을 누추기 위해서 afterCompletion 에 작성함, 여기서의 Exception 은 ControllerAdvice 에서 Ex 처리가 이미 되었기 때문에 실제 들어오지 않는다..(null)
        //api 경우는 response 내용을 통해 ex 내용을 알수 있지만 view 의 경우는 알수 없기 때문에 ControllerAdvice 에서 ex의 msg를 request 에 저장해 놓고 있다.
        //log.debug("afterCompletion called");

        String session = request.getSession().getId();
        String methodType = SpringUtil.getRequestMethodType();
        String url = SpringUtil.getRequestUrlQuery();
        String header = TypeConvertUtil.strMapToString(SpringUtil.getRequestHeaderMap());
        String params = TypeConvertUtil.strArrMapToString(SpringUtil.getRequestParameterMap());
        String requestBody = new String(((ContentCachingRequestWrapper)request).getContentAsByteArray());

        if(request.getRequestURI().startsWith("/api/")) {
            String responseBody = new String(((ContentCachingResponseWrapper)response).getContentAsByteArray());
            log.debug("\n--------------------\n[ReqRes Info from Interceptor]\nsession : {}\n({}) url : {}\nheader : {}\nparams : {}\n--> requestBody : {}\n<-- responseBody({}) : {}\n--------------------\n"
                    , session, methodType, url, header, params, StringUtils.hasText(requestBody)? "\n" + requestBody : "", response.getStatus(), StringUtils.hasText(responseBody)? "\n" + responseBody : "");

        } else {
            String exceptionMsg = ex != null ? ex.getMessage() : Optional.ofNullable(request.getAttribute(SpringUtil.getProperty("request.reserved.attribute.exceptionMsgForLogging", "EXCEPTION_MSG_FOR_LOGGING"))).map(Object::toString).orElse("");
            String responseModel = Optional.ofNullable(request.getAttribute("modelAndViewForLogging")).map(Object::toString).orElse("");
            log.debug("\n--------------------\n[ReqRes Info from Interceptor]\nsession : {}\n({}) url : {}\nheader : {}\nparams : {}\nexceptionMsg : {}\n--> requestBody : {}\n<-- modelAndView({}) : {}\n--------------------\n"
                    , session, methodType, url, header, params, exceptionMsg, StringUtils.hasText(requestBody)? "\n" + requestBody : "", response.getStatus(), StringUtils.hasText(responseModel)? "\n" + responseModel : "");
        }
    }

}

