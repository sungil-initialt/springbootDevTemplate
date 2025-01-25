package com.sptek.webfw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableApplicationCommonErrorResponse {
    // Application 내부 high-level 에러에 대한 응답에 Common 구조를 사용하겠다는 선언용
}