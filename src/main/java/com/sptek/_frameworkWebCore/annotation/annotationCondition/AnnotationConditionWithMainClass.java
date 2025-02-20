package com.sptek._frameworkWebCore.annotation.annotationCondition;

import com.sptek._frameworkWebCore.annotation.CheckMainClassAnnotation;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.Annotation;

public class AnnotationConditionWithMainClass implements Condition {

    @Override
    public boolean matches(ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {
        Environment environment = context.getEnvironment();
        String mainClassName = environment.getProperty("sun.java.command");

        if (metadata != null && metadata.isAnnotated(CheckMainClassAnnotation.class.getName())) {
            Object annotationClassName = metadata.getAnnotationAttributes(CheckMainClassAnnotation.class.getName()).get("value");
            boolean negate = (boolean) metadata.getAnnotationAttributes(CheckMainClassAnnotation.class.getName()).get("negate");

            if (annotationClassName instanceof Class) {
                Class<?> annotationClass = (Class<?>) annotationClassName;
                try {
                    Class<?> mainClass = Class.forName(mainClassName);
                    boolean isAnnotationPresent = mainClass.isAnnotationPresent((Class<? extends Annotation>) annotationClass);
                    return negate ? !isAnnotationPresent : isAnnotationPresent;
                } catch (ClassNotFoundException e) {
                    return negate;
                }
            }
        }
        return false;
    }
}