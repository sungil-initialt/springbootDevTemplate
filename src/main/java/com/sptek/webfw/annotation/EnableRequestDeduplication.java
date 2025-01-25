package com.sptek.webfw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE}) //클레스와 메소드에 모두 적용 가능
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableRequestDeduplication {
    //해당 어노테이션이 붙어 있는 경우만 중복 request 방지 AOP를 적용 시키기 위해 만든 어노테이션임.
}
