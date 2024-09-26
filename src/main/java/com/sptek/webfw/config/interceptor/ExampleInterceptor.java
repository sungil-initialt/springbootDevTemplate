package com.sptek.webfw.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/*
인터셉터를 만들때 레퍼런스용
<로그 결과>
getMethod : interceptorTest
getBeanType : com.sptek.webfw.example.api.api1.ApiTestController
getReturnType : org.springframework.http.ResponseEntity
hasMethodAnnotation : false
 */
@Component
@Slf4j
public class ExampleInterceptor implements HandlerInterceptor {
    
    @Override
    //컨트롤러 진입전 (인증, 권한 검사, 로깅 등의 작업 등)
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.debug("[Interceptor >>> ]");
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            /*
            log.debug("preHandle information : \n" +
                            "getMethod : {}\n" +
                            "getBeanType : {}\n" +
                            "getReturnType : {}\n" +
                            "hasMethodAnnotation : {}"
                    , handlerMethod.getMethod().getName()
                    , handlerMethod.getBeanType().getName()
                    , handlerMethod.getReturnType().getParameterType().getName()
                    , handlerMethod.hasMethodAnnotation(AnoInterceptorCheck.class)); //해당 메소드에 @AnoInterceptorCheck 어노테이션이 적용되어 있는지 여부
             */
        }
        return true;
    }

    @Override
    //컨트롤러 처리후 view 렌더링 전(모델에 데이터 추가, 응답 수정 등)
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            /*
            log.debug("postHandle information : \n" +
                            "getMethod : {}\n" +
                            "getBeanType : {}\n" +
                            "getReturnType : {}\n" +
                            "hasMethodAnnotation : {}"
                    , handlerMethod.getMethod().getName()
                    , handlerMethod.getBeanType().getName()
                    , handlerMethod.getReturnType().getParameterType().getName()
                    , handlerMethod.hasMethodAnnotation(AnoInterceptorCheck.class));
             */
        }
    }

    @Override
    //View가 렌더링되고 요청이 완료된 후 (주요 자원 정리, 예외 처리 로깅 등)
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            /*
            log.debug("afterCompletion information : \n" +
                            "getMethod : {}\n" +
                            "getBeanType : {}\n" +
                            "getReturnType : {}\n" +
                            "hasMethodAnnotation : {}"
                    , handlerMethod.getMethod().getName()
                    , handlerMethod.getBeanType().getName()
                    , handlerMethod.getReturnType().getParameterType().getName()
                    , handlerMethod.hasMethodAnnotation(AnoInterceptorCheck.class));
             */
        }
    }
}




