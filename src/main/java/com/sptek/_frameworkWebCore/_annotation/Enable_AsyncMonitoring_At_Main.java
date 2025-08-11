package com.sptek._frameworkWebCore._annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Enable_AsyncMonitoring_At_Main {
    //이름이 ...Log인 이유는 해당 어노테이션이 선언되어 있지 않더라도 HttpClientPool 관리를 위한 스케줄은 항상 동작하기 때문 (단지 로깅 처리 여부임)
    String value() default ""; // 입력 파람 값을 활용할 수 있도록 구성함
}
