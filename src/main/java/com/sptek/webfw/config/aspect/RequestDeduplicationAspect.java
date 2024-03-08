package com.sptek.webfw.config.aspect;

import com.sptek.webfw.code.ErrorCode;
import com.sptek.webfw.exceptionHandler.exception.ApiServiceException;
import com.sptek.webfw.exceptionHandler.exception.DuplicatedRequestException;
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

/*
Controller, RestController 모두 동작할 수 있게 만들었으나 브라우저(chrome, ie..) 등에서는 request 응답을 받기전
동일한 request를 또 보내면 이전 request를 부라우저가 cancel 처리하여 추후 response가 오더라도 받지를 않는다.
그래서 브라우저를 이용하여 페이지를 전화하는 경우의 request에서는 사실상 동작이 정상적으로 되지 않음.
 */

@Slf4j
@Aspect
@Component
public class RequestDeduplicationAspect
{
    private String REQUEST_ID_SET_NAME = "myRequestIdSet";

    @Pointcut("(@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)) && " +
            "(@within(com.sptek.webfw.anotation.AnoRequestDeduplication) || " +
            "@annotation(com.sptek.webfw.anotation.AnoRequestDeduplication))")
    public void duplicateRequestPrevent() {}

    @Before("duplicateRequestPrevent()")
    public void beforeRequest(JoinPoint joinPoint) {
        //to do when you need.
    }

    @Around("duplicateRequestPrevent()")
    public Object duplicateRequestCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest myRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        // GET 요청을 제외 하고 싶을때
        //if ("GET".equalsIgnoreCase(myRequest.getMethod())) {
        //    log.debug("duplicateRequestCheck : GET,  just pass through");
        //    return joinPoint.proceed();
        //}

        String controllerType = joinPoint.getTarget().getClass().isAnnotationPresent(RestController.class) ? "restController" : "viewController";
        HttpSession mySession = myRequest.getSession(true);
        String requestId = joinPoint.getSignature().toLongString();
        Set<String> myRequestIdSet;

        myRequestIdSet = Optional.ofNullable((Set<String>) mySession.getAttribute(REQUEST_ID_SET_NAME))
                .orElseGet(() -> Collections.synchronizedSet(new HashSet<>()));

        if(myRequestIdSet.contains(requestId)) {
            if(controllerType.equals("restController")) {
                return handleDuplicationForRestController();
            } else {
                return handleDuplicationForViewController(myRequest);
            }
        } else {
            myRequestIdSet.add(requestId);
            mySession.setAttribute(REQUEST_ID_SET_NAME, myRequestIdSet);
            log.debug("duplicateRequestCheck : saved myRequestId ({}) in session.", requestId);
            try {
                return joinPoint.proceed();

            } finally {  //exception 상황에서도 반드시 제거 필요
                myRequestIdSet.remove(requestId);
                //for check
                log.debug("duplicateRequestCheck : my request is done , clean requestId?  = {}", !((Set<String>) mySession.getAttribute(REQUEST_ID_SET_NAME)).contains(requestId));
            }
        }
    }

    private Object handleDuplicationForRestController() {
        log.debug("duplicateRequestCheck : RestController request is canceled");
        throw new ApiServiceException(ErrorCode.SERVICE_DUPLICATION_REQUEST_ERROR);
    }

    private Object handleDuplicationForViewController(HttpServletRequest request) {
        log.debug("duplicateRequestCheck : ViewController request is canceled");
        throw new DuplicatedRequestException(request);
    }
}