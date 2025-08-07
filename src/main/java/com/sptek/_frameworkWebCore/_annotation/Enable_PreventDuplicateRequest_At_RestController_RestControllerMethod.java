package com.sptek._frameworkWebCore._annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE}) //클레스와 메소드에 모두 적용 가능
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod {
    //해당 어노테이션이 적용된 RestController,  RestController-method 는 Deduplication 이 적용됨
}
