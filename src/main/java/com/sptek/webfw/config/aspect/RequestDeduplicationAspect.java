package com.sptek.webfw.config.aspect;

import com.sptek.webfw.code.ErrorCode;
import com.sptek.webfw.exceptionHandler.exception.DuplicatedRequestException;
import com.sptek.webfw.exceptionHandler.exception.ServiceException;
import com.sptek.webfw.util.ReqResUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

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
    private String REQUEST_SIGNATURE_NAME = "requestSignature";

    @Pointcut("(@within(org.springframework.stereotype.Controller) || " +
            "@within(org.springframework.web.bind.annotation.RestController)) && " +
            "(@within(com.sptek.webfw.anotation.AnoRequestDeduplication) || " +
            "@annotation(com.sptek.webfw.anotation.AnoRequestDeduplication))")
    public void pointCut() {}

    @Before("pointCut()")
    public void beforeRequest(JoinPoint joinPoint) {
        log.debug("AOP order : 3");
        //to do what you need.
    }

    @After("pointCut()")
    public void AfterRequest(JoinPoint joinPoint) {
        log.debug("AOP order : 5 (APO order 4 was caller)");
        //to do what you need.
    }

    @Around("pointCut()")
    public Object duplicateRequestCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("AOP order : 1");
        log.debug("sessionAttributeAll : {}", ReqResUtil.getSessionAttributesAll(true));
        HttpServletRequest currentRequest = ReqResUtil.getRequest();

        // GET 요청을 제외 하고 싶을때
        //if ("GET".equalsIgnoreCase(currentRequest.getMethod())) {
        //    log.debug("duplicateRequestCheck : GET,  just pass through");
        //    return joinPoint.proceed();
        //}

        String requestSignature = joinPoint.getSignature().toLongString();
        if(isDuplicatationCase(requestSignature)) {
            log.debug("RequestDeduplicationAspect: same RequestSignature exist and this request will be skipped.(nowMs:{}, existMs:{}"
                    , System.currentTimeMillis(), ReqResUtil.getSession(true).getAttribute(requestSignature));

            if(joinPoint.getTarget().getClass().isAnnotationPresent(RestController.class)) {
                return handleDuplicationForRestController();
            } else {
                return handleDuplicationForViewController(currentRequest);
            }
            
        } else {
            HttpSession currentSession = currentRequest.getSession(true);
            currentSession.setAttribute(requestSignature, System.currentTimeMillis() + 10*1000L);
            log.debug("duplicateRequestCheck : save new requestSignature ({} : 0), request ms is {}", requestSignature, System.currentTimeMillis());

            try {
                log.debug("AOP order : 2");
                return joinPoint.proceed(); //here!! return to caller method !! AOP order 4 is caller

            } finally {  //exception 상황에서도 반드시 expire Ms 업데이트 필요
                long newExpireMs = System.currentTimeMillis() + 2000L;
                currentSession.setAttribute(requestSignature, newExpireMs);

                log.debug("duplicateRequestCheck : my request is done, new expire ms is {}", newExpireMs);
            }
        }
    }

    private Object handleDuplicationForRestController() {
        log.debug("duplicateRequestCheck : RestController request is canceled");
        throw ServiceException.builder().errorCode(ErrorCode.SERVICE_DUPLICATION_REQUEST_ERROR).build();
    }

    private Object handleDuplicationForViewController(HttpServletRequest request) {
        log.debug("duplicateRequestCheck : ViewController request is canceled");
        throw new DuplicatedRequestException(request);
    }

    private boolean isDuplicatationCase(String requestSignature) {
        HttpSession currentSession = ReqResUtil.getSession(true);

        Long expiryTime;
        expiryTime = currentSession.getAttribute(requestSignature) == null ? 0: (Long)currentSession.getAttribute(requestSignature);

        if(expiryTime == 0) {
            log.debug("has no same requestSignature. hasToPass(false)");
            return false;

        } else if (expiryTime < System.currentTimeMillis()){
            log.debug("expiryTime({}) is smaller than curTime({}). hasToPass(false)", expiryTime, System.currentTimeMillis());
            return false;

        } else {
            log.debug("expiryTime({}) is bigger than curTime({}). hasToPass(ture)", expiryTime, System.currentTimeMillis());
            return true;
        }
        
        /*
        return Optional.ofNullable(currentRequest.getSession().getAttribute(requestSignature))
                .map(value -> (long) value)
                .filter(expiryTime -> expiryTime > System.currentTimeMillis())
                .isPresent();
        */
    }
}