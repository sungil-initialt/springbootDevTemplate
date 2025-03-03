package com.sptek._frameworkWebCore.annotation.annotationCondition;

import com.sptek._frameworkWebCore.annotation.HasAnnotationOnMain;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.Annotation;
import java.util.Map;

public class ConditionForHasAnnotationOnMain implements Condition {

    @Override
    public boolean matches(@NotNull ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(HasAnnotationOnMain.class.getName());

        if (attributes == null) {
            return false;
        }

        Class<?> annotationClass = (Class<?>) attributes.get("value");
        boolean negate = (boolean) attributes.get("negate");

        // 메인 애플리케이션 클래스 가져오기
        String mainClassName = context.getEnvironment().getProperty("sun.java.command");

        if (mainClassName == null) {
            return negate; // 메인 클래스를 찾을 수 없으면 negate 값에 따라 결정
        }

        try {
            Class<?> mainClass = Class.forName(mainClassName);
            boolean hasAnnotation = mainClass.isAnnotationPresent((Class<? extends Annotation>) annotationClass);
            return negate != hasAnnotation;

        } catch (ClassNotFoundException e) {
            return negate; // 클래스 로딩 실패 시 negate 값에 따라 처리
        }
    }
}