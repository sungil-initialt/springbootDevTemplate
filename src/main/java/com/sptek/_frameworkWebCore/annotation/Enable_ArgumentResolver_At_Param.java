package com.sptek._frameworkWebCore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_ArgumentResolver_At_Param {
    //HandlerMethodArgumentResolver 를 적용 할때 특정 어노테이션이 붙어 있는 경우만 적용 하는 조건을 걸기 위해 만든 어노테이션임
}
