package com.sptek.serviceName._global.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExampleAspect {
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)") // todo : 반드시 조건 수정 할것
    public void myPointCut() {}


    @Around("myPointCut()")
    public Object aroundXXX(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("aroundXXX");
        Object result = joinPoint.proceed();
        return result;
    }

    @Before("myPointCut()")
    public void beforeXXX(JoinPoint joinPoint) {
        log.debug("beforeXXX");
        //to do what you need.
    }

    @After("myPointCut()")
    public void afterXXX(JoinPoint joinPoint) {
        log.debug("afterXXX");
        //to do what you need.
    }
}