package com.sptek.webfw.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //메소드에 적용하기 위해
@Retention(RetentionPolicy.RUNTIME)
public @interface AnoInterceptorCheck {
    //인터셉터 를 적용할때 특정 어노테이션이 붙어 있는 메소드의 경우만 적용하는 조건을 걸기 위해 만든 어노테이션임 Example.
}
