package com.sptek.webfw.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
// todo: 해당 유틸은 Spring Component 임을 알고 코드 수정 및 사용 할것
public class SpringUtil {
    public static @NotNull HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    public static HttpServletResponse getResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
    }

    public static HttpSession getSession(boolean create) {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession(create);
    }
}
