package com.sptek._projectCommon.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Slf4j
@Aspect
//@Component (ex 임으로 off 처리함)
public class ExampleAspect {
    @Pointcut(
            "@within(org.springframework.web.bind.annotation.RestController)"
    ) // ex 요건!
    public void pointCut() {}

    @Around("pointCut()")
    public Object pointCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 실행 순서: 1
        log.debug("pointCutAround");
        Signature signature = joinPoint.getSignature();
        log.debug("메서드 이름: {}", signature.getName());
        log.debug("선언 타입명: {}", signature.getDeclaringTypeName());

        if (signature instanceof MethodSignature methodSignature) {
            log.debug("리턴 타입: {}", methodSignature.getReturnType().getSimpleName());
            Method method = methodSignature.getMethod();
            log.debug("실행 메서드: {}", method);
            if (method.isAnnotationPresent(Deprecated.class)) log.debug("@Deprecated 존재함");
        }

        log.debug("타겟 객체 : {}", joinPoint.getTarget().getClass().getName());
        log.debug("프록시 객체 : {}", joinPoint.getThis().getClass().getName());
        log.debug("kind : {}", joinPoint.getKind());

        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            log.debug("파라미터[{}] 값: {}", i, args[i]);
        }

        // 실행 순서: 2
        return joinPoint.proceed(); // 실행 순서: 4 (실제 method 내부)
    }


    @Before("pointCut()")
    public void pointCutBefore(JoinPoint joinPoint) {
        // 실행 순서: 4
        log.debug("pointCutBefore");
        //to do what you need.
    }

    @After("pointCut()")
    public void pointCutAfter(JoinPoint joinPoint) {
        // 실행 순서: 5
        log.debug("pointCutAfter");
        //to do what you need.
    }
}