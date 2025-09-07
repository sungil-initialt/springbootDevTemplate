package com.sptek._frameworkWebCore.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.openjdk.jol.info.ClassLayout;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
@Component

// todo: 해당 유틸은 Spring Component 로 선언된 클레스임을 알고 주의 해서 코드 수정 및 사용 할것
// todo: RequestContextHolder 를 어디서든 사용하기 위해서는 new org.springframework.web.filter.RequestContextFilter 순서를 최대한 높일것
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Getter
    private static ObjectMapper objectMapper;
    public SpringUtil(@Qualifier("objectMapperWithXssProtectHelper") ObjectMapper objectMapper) {
        SpringUtil.objectMapper = objectMapper;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        SpringUtil.applicationContext = applicationContext;
    }

    public static Class<?> findMainClassFromContext(@Nullable ApplicationContext applicationContext) {
        ApplicationContext ctx = (applicationContext != null) ? applicationContext : SpringUtil.applicationContext;
        if (ctx == null) {
            throw new IllegalStateException("ApplicationContext is null");
        }

        BeanFactory bf = ctx.getAutowireCapableBeanFactory();
        if (!(bf instanceof ListableBeanFactory lbf)) {
            throw new IllegalStateException("BeanFactory is not a ListableBeanFactory");
        }

        Class<?> mainClass = resolveMainClass(lbf);
        //log.debug("main class is {}", mainClass.getName());
        return mainClass;
    }

    // ConditionContext 기반: main class 반환 (실패 시 IllegalStateException)
    public static Class<?> findMainClassFromContext(ConditionContext conditionContext) {
        BeanFactory bf = conditionContext.getBeanFactory();
        if (!(bf instanceof ListableBeanFactory lbf)) {
            throw new IllegalStateException("BeanFactory is not a ListableBeanFactory");
        }
        Class<?> mainClass = resolveMainClass(lbf);
        //log.debug("main class is {}", mainClass.getName());
        return mainClass;
    }

    // 공통 구현부: @SpringBootConfiguration 붙은 타입을 찾아 CGLIB 상위 클래스로 승격
    private static Class<?> resolveMainClass(ListableBeanFactory lbf) {
        String[] names = lbf.getBeanNamesForAnnotation(SpringBootConfiguration.class);
        if (names.length == 0) {
            throw new IllegalStateException("@SpringBootConfiguration class not found");
        }

        // 다중 후보일 경우 첫 번째 사용 (정책 필요 시 여기서 커스터마이즈)
        String beanName = names[0];
        Class<?> type = lbf.getType(beanName);
        if (type == null) {
            throw new IllegalStateException("Cannot resolve type for bean: " + beanName);
        }

        // CGLIB 프록시 대비: 상위 클래스에 실제 애노테이션이 있으면 승격
        Class<?> superClass = type.getSuperclass();
        if (superClass != null
                && superClass != Object.class
                && AnnotatedElementUtils.hasAnnotation(superClass, SpringBootConfiguration.class)) {
            return superClass;
        }
        return type;
    }

    public static <T> T getSpringBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

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

    public static String getInstanceMemoryInfo(Object object) {
        // implementation 'org.openjdk.jol:jol-core:0.17' 사용
        return object.getClass().getSimpleName() + " instance memory info\n" + ClassLayout.parseInstance(object).toPrintable();
    }
}
