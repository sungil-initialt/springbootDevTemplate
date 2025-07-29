package com.sptek._frameworkWebCore.interceptor;

import com.sptek._frameworkWebCore.annotation.Enable_XssProtectForView_At_ControllerMethod;
import com.sptek._frameworkWebCore.support.XssEscapeSupport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

//@HasAnnotationOnMain_InBean(TestAnnotation_InAll.class) //HasAnnotationOnMain 설정 으로 처리 하려다 성능 및 원본 수정등의 상황을 고려 하여 controller Annotation 적용 으로 변경함
@Slf4j
@RequiredArgsConstructor
@Component

public class ViewXssProtectInterceptor implements HandlerInterceptor {
    private final XssEscapeSupport xssEscapeSupport;

    @Override
    public void postHandle(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull Object handler,
            ModelAndView modelAndView
    ) {
        if (handler instanceof HandlerMethod handlerMethod && modelAndView != null) {
            if (handlerMethod.hasMethodAnnotation(Enable_XssProtectForView_At_ControllerMethod.class)) {
                log.debug("ModelView Xss Protector On");
                Map<String, Object> model = modelAndView.getModel();
                model.replaceAll((key, value) -> xssEscapeSupport.escape(value));
            }
        }
    }
}