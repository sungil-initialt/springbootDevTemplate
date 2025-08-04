package com.sptek._frameworkWebCore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) // Annotation is retained at runtime
public @interface Enable_VisitHistoryLogging_At_Main {
    String value() default "oncePerDay";; // all: 모든 이력, oncePerDay: 방문자별(쿠키기준) 하루 한번만 기록
}
