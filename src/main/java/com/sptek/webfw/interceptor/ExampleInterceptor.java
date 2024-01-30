package com.sptek.webfw.interceptor;

import com.sptek.webfw.anotation.AnoInterceptorCheck;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/*
인터셉터를 만들때 레퍼런스용
 */
@Component
@Slf4j
public class ExampleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            log.debug("ExampleInterceptor preHandle information : \n" +
                            "getMethod : {}\n" +
                            "getBeanType : {}\n" +
                            "getReturnType : {}\n" +
                            "hasMethodAnnotation : {}"
                    , handlerMethod.getMethod().getName()
                    , handlerMethod.getBeanType().getName()
                    , handlerMethod.getReturnType().getParameterType().getName()
                    , handlerMethod.hasMethodAnnotation(AnoInterceptorCheck.class)); //해당 메소드에 @AnoInterceptorCheck 어노테이션이 적용되어 있는지 여부

            //todo : 위 정보를 이용하여 원하는 내용을 작성
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            log.debug("ExampleInterceptor postHandle information : \n" +
                            "getMethod : {}\n" +
                            "getBeanType : {}\n" +
                            "getReturnType : {}\n" +
                            "hasMethodAnnotation : {}"
                    , handlerMethod.getMethod().getName()
                    , handlerMethod.getBeanType().getName()
                    , handlerMethod.getReturnType().getParameterType().getName()
                    , handlerMethod.hasMethodAnnotation(AnoInterceptorCheck.class));
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            log.debug("ExampleInterceptor afterCompletion information : \n" +
                            "getMethod : {}\n" +
                            "getBeanType : {}\n" +
                            "getReturnType : {}\n" +
                            "hasMethodAnnotation : {}"
                    , handlerMethod.getMethod().getName()
                    , handlerMethod.getBeanType().getName()
                    , handlerMethod.getReturnType().getParameterType().getName()
                    , handlerMethod.hasMethodAnnotation(AnoInterceptorCheck.class));
        }
    }
}




