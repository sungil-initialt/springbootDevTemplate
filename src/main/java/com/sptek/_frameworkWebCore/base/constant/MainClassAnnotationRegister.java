package com.sptek._frameworkWebCore.base.constant;

import com.sptek._frameworkWebCore.util.LoggingUtil;
import com.sptek._frameworkWebCore.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j

// Application main Class 에 적용된 타멧 페키지 Annotation  정보를 모두 가지고 있는 역할
public class MainClassAnnotationRegister {
    private static Map<String, Map<String, Object>> mainClassAnnotationRegister = Collections.emptyMap();

    public MainClassAnnotationRegister(ApplicationContext applicationContext) throws Exception {
        synchronized (MainClassAnnotationRegister.class) {
            if (!mainClassAnnotationRegister.isEmpty()) return;

            Class<?> mainClass = SpringUtil.findMainClassFromContext(applicationContext);
            Map<String, Map<String, Object>> temp = new HashMap<>();
            for (Annotation annotation : mainClass.getAnnotations()) {
                String name = annotation.annotationType().getName();
                if (name.startsWith(CommonConstants.FRAMEWORK_ANNOTATION_PACKAGE_NAME)) {
                    Map<String, Object> attrs = AnnotationUtils.getAnnotationAttributes(annotation, false);
                    temp.put(name, Map.copyOf(attrs));
                }
            }
            mainClassAnnotationRegister = Map.copyOf(temp);
            log.debug(LoggingUtil.makeBaseForm("MainClass Annotation Register", mainClassAnnotationRegister.toString(), CommonConstants.FW_START_LOG_TAG));
        }
    }

    public static boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return mainClassAnnotationRegister.containsKey(annotation.getName());
    }

    public static Map<String, Object> getAnnotationAttributes(Class<? extends Annotation> annotation) {
        return mainClassAnnotationRegister.getOrDefault(annotation.getName(), Map.of());
    }
}

