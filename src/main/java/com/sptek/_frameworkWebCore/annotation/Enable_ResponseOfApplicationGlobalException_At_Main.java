package com.sptek._frameworkWebCore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_ResponseOfApplicationGlobalException_At_Main {
    // Application 내부의 high-level 에러 응답시 공통 구조 사용
}