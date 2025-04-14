package com.sptek._frameworkWebCore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableDetailLog_InMain_Controller_ControllerMethod {
    String value() default ""; // 입력 파람 값을 활용할 수 있도록 구성함
}

