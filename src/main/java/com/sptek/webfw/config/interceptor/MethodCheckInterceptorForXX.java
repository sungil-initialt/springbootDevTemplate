package com.sptek.webfw.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MethodCheckInterceptorForXX implements HandlerInterceptor {

    @Override
    //해당 인터셉터가 잘 적용되는 확인하기 위한 예시로 실제 필요한 내용으로 수정하면 된다.
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        log.debug("[Interceptor >>> ]");
        log.debug("method type : {}", request.getMethod());
        //do more what you want.
        return true;
    }
}

