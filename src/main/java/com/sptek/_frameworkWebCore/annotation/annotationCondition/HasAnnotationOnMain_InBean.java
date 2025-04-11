package com.sptek._frameworkWebCore.annotation.annotationCondition;

import com.sptek._frameworkWebCore.annotation.HasAnnotationOnMain_InBeans;
import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

@Target({ ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(HasAnnotationOnMain_InBeans.class)
@Conditional(ConditionForHasAnnotationOnMain.class)
public @interface HasAnnotationOnMain_InBean {
    Class<? extends Annotation> value();
    boolean negate() default false; // true면 어노테이션이 없을 때 Bean 생성 됨
}
