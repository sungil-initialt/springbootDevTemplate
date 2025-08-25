package com.sptek._frameworkWebCore._annotation;

import org.springframework.scheduling.annotation.Async;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Async
public @interface Enable_CompletableFutureAsync_At_ServiceMethod {
}
