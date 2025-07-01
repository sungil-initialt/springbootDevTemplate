package com.sptek._frameworkWebCore.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
@Component
// todo: 해당 유틸은 Spring Component 로 선언된 클레스임을 알고 주의 해서 코드 수정 및 사용 할것
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    public static <T> T getSpringBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

//    // MainClassAnnotationRegister 를 사용하는 방법으로 변경
//    public static boolean hasAnnotationOnMain(Class<? extends Annotation> annotation){
//        Environment environment = Objects.requireNonNull(SpringUtil.applicationContext).getEnvironment();
//        String mainClassName = environment.getProperty("sun.java.command"); // todo: 일부 JVM 환경 에서 동작 하지 않을 수도 있음
//        //log.debug("mainClassName: {}", mainClassName);
//
//        try {
//            Class<?> mainClass = Class.forName(mainClassName);
//            return mainClass.isAnnotationPresent(annotation);
//        } catch (ClassNotFoundException e) {
//            return false;
//        }
//    }

    public static HttpServletRequest getRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes.getRequest();
        }
        throw new IllegalStateException("No request bound to current thread");
    }

    public static HttpServletResponse getResponse() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            HttpServletResponse response = sra.getResponse();
            if (response != null) {
                return response;
            }
            throw new IllegalStateException("No response available for current request");
        }
        throw new IllegalStateException("No request bound to current thread");
    }

    public static HttpSession getSession(boolean create) {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes sra) {
            HttpServletRequest request = sra.getRequest();
            return request.getSession(create);
        }
        throw new IllegalStateException("No request bound to current thread");
    }

    public static Object getApplicationProperty(String key) {
        // todo: 일부 JVM 환경 에서 동작 하지 않을 수도 있음
        Environment environment = Objects.requireNonNull(applicationContext).getEnvironment();
        return environment.getProperty(key);
    }
}
