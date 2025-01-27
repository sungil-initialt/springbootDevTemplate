package com.sptek._frameworkWebCore.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.lang.annotation.Annotation;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
// todo: 해당 유틸은 Spring Component 임을 알고 코드 수정 및 사용 할것
public class SpringUtil {

    public static boolean hasAnnotationOnMainClass(HttpServletRequest request, Class<? extends Annotation> annotation){
        Environment environment = Objects.requireNonNull(WebApplicationContextUtils.getWebApplicationContext(request.getServletContext())).getEnvironment();
        String mainClassName = environment.getProperty("sun.java.command");
        log.debug("mainClassName: {}", mainClassName);

        try {
            Class<?> mainClass = Class.forName(mainClassName);
            return mainClass.isAnnotationPresent(annotation);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


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
