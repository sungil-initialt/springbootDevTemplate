package com.sptek.webfw.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MethodCheckInterceptorForXX implements HandlerInterceptor {

    @Override
    //해당 인터셉터가 잘 적용되는 확인하기 위한 예시 Interceptor로 실제 필요한 내용으로 수정하면 된다.
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("called MethodCheckInterceptorForXX : {}", request.getMethod());

        return true;
    }
}

