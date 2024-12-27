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
    public void postHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        //log.debug("postHandle called");
        request.setAttribute(SpringUtil.getProperty("request.reserved.attribute.modelAndViewForLogging", "MODEL_AND_VIEW_FOR_LOGGING"), modelAndView != null ? modelAndView.getModel() : Collections.emptyMap());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable Exception ex) throws Exception {
        //afterCompletion는 controller 레이어에서 에러가 나더라도 호출됨으로 이곳에서 로킹하는 것이 적합함
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
            String responseModel = String.valueOf(request.getAttribute("modelAndViewForLogging"));
            log.debug("\n--------------------\n[ReqRes Info from Interceptor]\nsession : {}\n({}) url : {}\nheader : {}\nparams : {}\n--> requestBody : {}\n<-- modelAndView({}) : {}\n--------------------\n"
                    , session, methodType, url, header, params, StringUtils.hasText(requestBody)? "\n" + requestBody : "", response.getStatus(), StringUtils.hasText(responseModel)? "\n" + responseModel : "");
        }
    }

}

