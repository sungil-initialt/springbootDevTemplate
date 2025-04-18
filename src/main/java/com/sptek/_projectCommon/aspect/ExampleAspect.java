package com.sptek._projectCommon.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
//@Component (ex 임으로 off 처리함)
public class ExampleAspect {
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)") // todo : 반드시 조건 수정 할것
    public void myPointCut() {}


    @Around("myPointCut()")
    public Object aroundXXX(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("aroundXXX");
        return joinPoint.proceed();
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