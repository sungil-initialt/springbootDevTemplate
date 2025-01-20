package com.sptek.webfw.config.interceptor;

import com.sptek.webfw.base.constant.CommonConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;

@Slf4j
//@Component
public class ViewErrorLogSupportInterceptor implements HandlerInterceptor {
    // 해당 interceptor 는 어딘가에서든? 로그 정보로 활용할 수 있도록 view 에러와 관련된 model, 및 EX message를 저장해 준다.

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        log.debug("preHandle called");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable ModelAndView modelAndView) {
        log.debug("postHandle called");
        //필터의 response 에서는 modelAndView 정보를 가져올 방법이 없기 때문에 이곳에서 저장해서 처리함
        request.setAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_MODELANDVIEW, modelAndView != null ? modelAndView : Collections.emptyMap());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, @Nullable Exception ex) {
        log.debug("afterCompletion called");

        //api 경우는 response 내용을 통해 ex 내용을 알수 있지만 view 의 경우는 알수 없기 때문에 저장하도록 함
        //위치적으로는 viewResolver 단계에서도 에러가 날수 있기 때문에 afterCompletion 에 적용함
        //에러가 발생하는 케이스에 따라 ApplicationGlobalExceptionHandler 에서 이미 저장되는 케이스도 있음
        if (ex != null) {
            request.setAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE, ex.getMessage());
        }

    }

}

