package com.sptek._frameworkWebCore.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Annotation is retained at runtime
@Target({
        ElementType.TYPE,           // Classes, interfaces, enums
        ElementType.METHOD,         // Methods
})
public @interface EnableDetailLogFilter {
    String value() default ""; // 단순히 어노테이션이 적용되어 있는지 뿐 아니라 더불어 어떤 값으로 설정되어 있는지를 넘겨줄 필요가 있을때 활용
}

