package com.sptek._frameworkWebCore._annotation.annotationCondition;

import com.sptek._frameworkWebCore.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.annotation.AnnotatedElementUtils;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ConditionForHasAnnotationOnMain implements Condition {

    @Override
    public boolean matches(@NotNull ConditionContext context, AnnotatedTypeMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(HasAnnotationOnMain_At_Bean.class.getName());
        if (attributes == null) return false;

        Class<?> annotationClass = (Class<?>) attributes.get("value");
        boolean negate = (boolean) attributes.get("negate");

        Class<?> mainClass = SpringUtil.findMainClassFromContext(context);
        boolean hasAnnotation = mainClass.isAnnotationPresent((Class<? extends Annotation>) annotationClass);
        return negate != hasAnnotation;
    }
}