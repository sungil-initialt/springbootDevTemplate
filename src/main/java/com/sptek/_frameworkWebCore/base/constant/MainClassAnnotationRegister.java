package com.sptek._frameworkWebCore.base.constant;

import com.sptek._frameworkWebCore.util.SptFwUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j

// Application main Class 에 적용된 타멧 페키지 Annotation  정보를 모두 가지고 있는 역할
public class MainClassAnnotationRegister {
    private static final String TARGET_ANNOTATION_PACKAGE = "com.sptek._frameworkWebCore.annotation";

    // 한 번 초기화된 후에 변경 여지가 없기 때문에 속도 측면에서 유리하고 Thread Safe 한 unmodifiableMap을 사용함 (ConcurrentHashMap을 쓰지 않은 이유)
    private static Map<String, Map<String, Object>> mainClassAnnotationRegister = Collections.emptyMap();

    public MainClassAnnotationRegister(ApplicationContext applicationContext) throws Exception{
        if (!mainClassAnnotationRegister.isEmpty()) {
            return;
        }

        Environment environment = Objects.requireNonNull(applicationContext).getEnvironment();
        String mainClassName = environment.getProperty("sun.java.command"); // todo: 일부 JVM 환경 에서 동작 하지 않을 수도 있음
        Class<?> mainClass = Class.forName(mainClassName);

        Map<String, Map<String, Object>> tempMainAnnotationRegister = new HashMap<>();

        for (Annotation annotation : mainClass.getAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            String annotationFullName = annotationType.getName();

            if (annotationFullName.startsWith(TARGET_ANNOTATION_PACKAGE)) {
                Map<String, Object> attributes = AnnotationUtils.getAnnotationAttributes(annotation, false);
                tempMainAnnotationRegister.put(annotationFullName, attributes);
            }
        }

        mainClassAnnotationRegister = Collections.unmodifiableMap(tempMainAnnotationRegister);
        log.debug(SptFwUtil.convertSystemNotice("Main Class Annotation List", mainClassAnnotationRegister.toString()));
    }

    public static boolean hasAnnotation(Class<? extends Annotation> annotation) {
         return mainClassAnnotationRegister.containsKey(annotation.getName());
    }

    public static Map<String, Object> getAnnotationAttributes(Class<? extends Annotation> annotation) {
        if (mainClassAnnotationRegister.containsKey(annotation.getName())) {
            return mainClassAnnotationRegister.get(annotation.getName());

        } else {
            return Map.of();
        }
    }
}
