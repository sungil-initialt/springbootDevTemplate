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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.Annotation;
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

    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    public static boolean hasAnnotationOnMain(Class<? extends Annotation> annotation){
        Environment environment = Objects.requireNonNull(SpringUtil.applicationContext).getEnvironment();
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
