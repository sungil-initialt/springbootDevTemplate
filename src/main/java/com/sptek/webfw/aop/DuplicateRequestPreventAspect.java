package com.sptek.webfw.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Aspect
@Component
public class DuplicateRequestPreventAspect
{
    private String REQUEST_ID_SET_NAME = "myRequestIdSet";

    @Pointcut("(@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)) && " +
            "(@within(com.sptek.webfw.anotation.AnoDuplicationRequestPrevent) || " +
            "@annotation(com.sptek.webfw.anotation.AnoDuplicationRequestPrevent))")
    public void duplicateRequestPrevent() {}

    @Before("duplicateRequestPrevent()")
    public void beforeRequest(JoinPoint joinPoint) {
        //to do when you need.
    }

    @Around("duplicateRequestPrevent()")
    public Object duplicateRequestCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest myRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // GET은 처리에서 제외 (중요한 요청이 아닌경우가 보통이고 img, css 등 static 리소스를 제외하기 위함)
        //if ("GET".equalsIgnoreCase(myRequest.getMethod())) {
        //    log.debug("duplicateRequestCheck : GET,  just pass through");
        //    return joinPoint.proceed();
        //}

        String controllerType = joinPoint.getTarget().getClass().isAnnotationPresent(RestController.class) ? "restController" : "viewController"; //타입에 따라 response 다르게 처리
        HttpSession mySession = myRequest.getSession(true);
        String requestId = joinPoint.getSignature().toLongString();
        Set<String> myRequestIdSet;

        myRequestIdSet = Optional.ofNullable((Set<String>) mySession.getAttribute(REQUEST_ID_SET_NAME))
                .orElseGet(() -> Collections.synchronizedSet(new HashSet<>()));

        if(myRequestIdSet.contains(requestId)) {
            if(controllerType.equals("restController")) {
                return handleDuplicateForRestController();
            } else {
                return handleDuplicateForViewController();
            }
        } else {
            myRequestIdSet.add(requestId);
            mySession.setAttribute(REQUEST_ID_SET_NAME, myRequestIdSet);
            try {
                return joinPoint.proceed();

            } finally {  //exception 상황에서도 반드시 제거 필요
                myRequestIdSet.remove(requestId);
                //for check
                log.debug("duplicateRequestCheck : my request is done , clean requestId?  = {}", !((Set<String>) mySession.getAttribute(REQUEST_ID_SET_NAME)).contains(requestId));
            }
        }
    }

    private Object handleDuplicateForRestController() {
        //HttpStatus.OK, "SE202", "Duplication Request Error Exception"
        log.debug("duplicateRequestCheck : RestController request is canceled");
        return null;
        //throw new ApiServiceException(ApiErrorCode.SERVICE_DUPLICATION_REQUEST_ERROR);
    }

    private Object handleDuplicateForViewController() {
        // 중복 요청에 대한 응답 처리
        log.debug("duplicateRequestCheck : ViewController request is canceled");
        throw new RuntimeException("duplicateRequestCheck : ViewController request is canceled");
    }
}