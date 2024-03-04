package com.sptek.webfw.aop;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Aspect
@Component
public class DuplicateRequestPreventAspect
{
    private String controllerType;
    private Set<String> requestSet = Collections.synchronizedSet(new HashSet<>());

    @Pointcut("(@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)) && " +
            "(@within(com.sptek.webfw.anotation.AnoDuplicationRequestPrevent) || " +
            "@annotation(com.sptek.webfw.anotation.AnoDuplicationRequestPrevent))")
    public void request() {}



    @Before("request()")
    public void beforeRequest(JoinPoint joinPoint) {
        // 클래스 어노테이션 확인
        Class<?> clazz = joinPoint.getTarget().getClass();
        String controllerType = clazz.isAnnotationPresent(RestController.class) ? "restController" : "viewController";
    }

    @Around("request()")
    public Object duplicateRequestCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String httpMethod = request.getMethod();

        // GET 제외
        if ("GET".equalsIgnoreCase(httpMethod)) {
            return joinPoint.proceed();
        }

        String requestId = joinPoint.getSignature().toLongString();
        if (requestSet.contains(requestId)) {
            return handleDuplicateRequest();
        }
        requestSet.add(requestId);
        try {
            return joinPoint.proceed();
        } finally {
            requestSet.remove(requestId);
        }
    }

    private ResponseEntity<Object> handleDuplicateRequest() { //컨트롤 종류에 따라 응답을 달리하도록 수정 필요!
        // 중복 요청에 대한 응답 처리
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("중복된 요청 입니다");
    }
}