package com.sptek._frameworkWebCore.annotation;

import com.sptek._frameworkWebCore.annotation.annotationCondition.ConditionForHasAnnotationOnMain;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionForHasAnnotationOnMain.class)
public @interface HasAnnotationOnMain {

    Class<? extends Annotation> value(); // 검사할 애너테이션 타입

    boolean negate() default false; // true면 애너테이션이 없을 때만 활성화
}
