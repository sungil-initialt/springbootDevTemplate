package com.sptek._frameworkWebCore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_ResponseOfViewGlobalException_At_ViewController {
    // view Controller 에러 발생시 공통 에러 플로우 및 공통 에러 페이이 적용
}