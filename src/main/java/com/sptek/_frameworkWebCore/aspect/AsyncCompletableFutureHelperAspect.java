package com.sptek._frameworkWebCore.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.util.concurrent.CompletableFuture;

@Slf4j
//@Aspect
//@Component
// fw 의 api Controller 가 object 타입으로 넘긴 결과를 ApiCommonSuccessResponseDto 형태로 변형하고, ResponseEntity 를 구성해 전송 하도록 처리.
public class AsyncCompletableFutureHelperAspect {
    @Pointcut(
            "@within(org.springframework.stereotype.Service) && " +
                    "@annotation(com.sptek._frameworkWebCore._annotation.Enable_AsyncResponse_At_RestControllerMethod)"
    )
    public void myPointCut() {}

    @Around("myPointCut()")
    public Object adaptReturn(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        return CompletableFuture.completedFuture(result);
    }

    @Before("myPointCut()")
    public void before(JoinPoint joinPoint) {
        //to do what you need.
    }

    @After("myPointCut()")
    public void after(JoinPoint joinPoint) {
        //to do what you need.
    }
}