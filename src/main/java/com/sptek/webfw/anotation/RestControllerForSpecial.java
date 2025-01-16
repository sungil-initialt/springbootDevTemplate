package com.sptek.webfw.anotation;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@RestController // 메타 애노테이션으로 @RestController 포함
public @interface RestControllerForSpecial {
    // @RestController 과 이름만 다른 어노테이션을 만든것임
}
