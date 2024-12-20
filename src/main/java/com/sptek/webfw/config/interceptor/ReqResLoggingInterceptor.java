package com.sptek.webfw.config.interceptor;

import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        request.setAttribute("modelAndViewForLogging", modelAndView != null ? modelAndView.getModel() : Collections.emptyMap());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        //afterCompletion는 controller 레이어에서 에러가 나더라도 호출됨으로 이곳에서 로킹하는 것이 적합함

        String session = request.getSession().getId();
        String methodType = ReqResUtil.getRequestMethodType(request);
        String url = ReqResUtil.getRequestUrlString(request);
        String header = TypeConvertUtil.strMapToString(ReqResUtil.getRequestHeaderMap(request));
        String params = TypeConvertUtil.strArrMapToString(ReqResUtil.getRequestParameterMap(request));
        String requestBody = new String(((ContentCachingRequestWrapper)request).getContentAsByteArray());

        if(request.getRequestURI().startsWith("/api")) {
            String responseBody = new String(((ContentCachingResponseWrapper)response).getContentAsByteArray());
            log.debug("\n--------------------\n[Request Info Interceptor]\nsession : {}\n({}) url : {}\nheader : {}\nparams : {} \n--> requestBody : {}\n<-- responseBody : {}\n--------------------\n"
                    , session, methodType, url, header, params, StringUtils.hasText(requestBody)? "\n" + requestBody : "", StringUtils.hasText(responseBody)? "\n" + responseBody : "");

        } else {
            String responseModel = String.valueOf(request.getAttribute("modelAndViewForLogging"));
            log.debug("\n--------------------\n[Request Info Interceptor]\nsession : {}\n({}) url : {}\nheader : {}\nparams : {} \n--> requestBody : {}\n<-- modelAndView : {}\n--------------------\n"
                    , session, methodType, url, header, params, StringUtils.hasText(requestBody)? "\n" + requestBody : "", StringUtils.hasText(responseModel)? "\n" + responseModel : "");
        }
    }
}

