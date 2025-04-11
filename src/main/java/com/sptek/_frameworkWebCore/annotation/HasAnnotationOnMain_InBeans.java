package com.sptek._frameworkWebCore.annotation;

import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HasAnnotationOnMain_InBeans {
    HasAnnotationOnMain_InBean[] value();
}