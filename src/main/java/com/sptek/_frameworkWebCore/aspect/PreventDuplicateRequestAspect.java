package com.sptek._frameworkWebCore.aspect;

import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._projectCommon.commonObject.code.ServiceErrorCodeEnum;
import com.sptek._frameworkWebCore.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@Aspect
@Component
// 정상적인 동작이 되려면 해당 사용자에 대한 session이 이미 존재한 상태여야 함(대부분의 경우 이미 존재할듯)
public class PreventDuplicateRequestAspect {
    private final Object SESSION_LOCK = new Object(); // 클래스 단위 공유 락 객체
    private final long DEDUPLICATION_DEFAULT_MS = 1000L*10; //해당 메소드 처리 시간이 실제 얼마가 걸릴지 알수 없기 때문에 우선 이 Sec 동안 중복 요청 불가 처리함 (해당 시간 내에도 처리 되지 못하면 중복 방어가 더이상 안됨)
    private final long DEDUPLICATION_EXTRA_MS = 1000L; //해당 메소드 처리가 종료되면 남은 초기 시간과 관계 없이 새로운 시간으로 중복 처리 방어함 (종료가 되었더라도 1초 정도는 중복 방어함)

    //기능의 특성상 @Controller 에는 적용이 어려워 보임, @RestController 에만 적용하는 것으로 처리
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
            "&& (@within(com.sptek._frameworkWebCore.annotation.Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod) || @annotation(com.sptek._frameworkWebCore.annotation.Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod))")
    public void myPointCut() {}


    @Around("myPointCut()")
    public Object duplicateRequestCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        //log.debug("AOP order : 1");
        //log.debug("sessionAttributeAll : {}", ReqResUtil.getSessionAttributesAll(true));


        // GET 요청을 제외 하고 싶을때
        //HttpServletRequest currentRequest = SpringUtil.getRequest();
        //if ("GET".equalsIgnoreCase(currentRequest.getMethod())) {
        //    log.debug("duplicateRequestCheck : GET,  just pass through");
        //    return joinPoint.proceed();
        //}

        // 해당 메소드의 서명값으로 식별자로 사용할 수 있다 (동일 메소드의 경우 항상 동일 값)
        String requestedMethodSignature = joinPoint.getSignature().toLongString();

        if(isDuplicationCase(requestedMethodSignature)) {
            if(joinPoint.getTarget().getClass().isAnnotationPresent(RestController.class)) {
                return handleDuplicationForRestController();
            } else {
                //todo: 처리 영역은 만들어 놓았지만.. 화면이 있는 @Controller 에는 적용이 어려울 듯
                return "";
            }

        } else {
            try {
                //log.debug("AOP order : 2");
                return joinPoint.proceed(); // --> @Before --> origin caller --> @After 순으로 진행됨

            } finally {  //exception 상황에서도 반드시 expire Ms 업데이트 필요
                long newExpireMs = System.currentTimeMillis() + DEDUPLICATION_EXTRA_MS;
                SpringUtil.getRequest().getSession(true).setAttribute(requestedMethodSignature, newExpireMs);
                log.debug("my request is done, set new expiryTime ({})", newExpireMs);
            }
        }
    }


    @Before("myPointCut()")
    public void beforeRequest(JoinPoint joinPoint) {
        //log.debug("AOP order : 3");
        //to do what you need.
    }


    @After("myPointCut()")
    public void afterRequest(JoinPoint joinPoint) {
        //log.debug("AOP order : 5 (order 4 is caller method)");
        //to do what you need.
    }


    private Object handleDuplicationForRestController() throws ServiceException {
        throw new ServiceException(ServiceErrorCodeEnum.DUPLICATION_REQUEST_ERROR);
    }


    // 좀더 쓰레드 세이프 하게 구성 하기 위해서 세션 에서 requestSignature 확인과 requestSignature 셋팅을 synchronized 방식 으로 수정함
    private boolean isDuplicationCase(String requestSignature) {
        synchronized (SESSION_LOCK) {
            Object attr = SpringUtil.getRequest().getSession(true).getAttribute(requestSignature);
            long expiryTime = (attr instanceof Long) ? (long) attr : 0;

            if (expiryTime == 0 || expiryTime < System.currentTimeMillis()) {
                long newExpiryTime = System.currentTimeMillis() + DEDUPLICATION_DEFAULT_MS;
                SpringUtil.getRequest().getSession(true).setAttribute(requestSignature, newExpiryTime);
                log.debug("No alive request. Accepted. Set new expiryTime: {}", newExpiryTime);
                return false;
            } else {
                log.debug("Reject request. Already in session. expiryTime: {}", expiryTime);
                return true;
            }
        }
    }
}