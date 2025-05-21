package com.sptek._projectCommon.interceptor;

import com.sptek._frameworkWebCore.annotation.TestAnnotation_InAll;
import com.sptek._frameworkWebCore.util.RequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        log.debug("[ Interceptor >>> ({}) {} ]", request.getMethod(), RequestUtil.getRequestUrlQuery(request));

        if (handler instanceof HandlerMethod handlerMethod) {
            log.debug("preHandle information : \n" +
                            "getMethod : {}\n" +
                            "getBeanType : {}\n" +
                            "getReturnType : {}\n" +
                            "hasMethodAnnotation : {}"
                    , handlerMethod.getMethod().getName()
                    , handlerMethod.getBeanType().getName()
                    , handlerMethod.getReturnType().getParameterType().getName()
                    , handlerMethod.hasMethodAnnotation(TestAnnotation_InAll.class) //해당 메소드에 특정 어노테이션이 적용되어 있는지 여부
            );
            // do what you want.
        }
        return true;
    }

    @Override
    //컨트롤러 처리후 view 렌더링 전(모델에 데이터 추가, 응답 수정 등)
    public void postHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, ModelAndView modelAndView) {
        if (handler instanceof HandlerMethod) {
            log.debug("postHandle");
            // do what you want.
        }
    }

    @Override
    //View가 렌더링되고 요청이 완료된 후 (주요 자원 정리, 예외 처리 로깅 등)
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) {
        if (handler instanceof HandlerMethod) {
            log.debug("afterCompletion");
            // do what you want,
        }
    }
}




