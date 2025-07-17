package com.sptek._frameworkWebCore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Annotation is retained at runtime
@Target({
        ElementType.TYPE,           // Classes, interfaces, enums
        ElementType.METHOD,         // Methods
        ElementType.FIELD,          // Fields
        ElementType.CONSTRUCTOR,    // Constructors
        ElementType.PARAMETER,      // Parameters
        ElementType.LOCAL_VARIABLE, // Local variables
        ElementType.ANNOTATION_TYPE // Other annotations
})
public @interface TestAnnotation_At_All {
    // 실제 어노테이션 생성전 단순 테스트 용으로 활용함
    String value() default "";
}

