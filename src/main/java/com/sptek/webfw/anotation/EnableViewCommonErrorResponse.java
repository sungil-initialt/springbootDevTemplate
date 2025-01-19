package com.sptek.webfw.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableViewCommonErrorResponse {
    // API 응답시 템플릿의 기본 ResponseEntity 구조를 사용하겠다는 선언용
}