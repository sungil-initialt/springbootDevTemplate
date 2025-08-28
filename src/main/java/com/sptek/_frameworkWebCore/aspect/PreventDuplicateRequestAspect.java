package com.sptek._frameworkWebCore.aspect;

import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.util.SpringUtil;
import com.sptek._projectCommon.commonObject.code.ServiceErrorCodeEnum;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@Aspect
@Order(1)
@Component
// 정상적인 동작이 되려면 해당 사용자에 대한 session이 이미 존재한 상태여야 함(대부분의 경우 이미 존재할듯)
public class PreventDuplicateRequestAspect {
    private final ConcurrentHashMap<String, ReentrantLock> SESSION_LOCK = new ConcurrentHashMap<>();
    private final long DEDUPLICATION_DEFAULT_MS = 10_000L; //해당 메소드 처리 시간이 실제 얼마가 걸릴지 알수 없기 때문에 우선 이 Sec 동안 중복 요청 불가 처리함 (해당 시간 내에도 처리 되지 못하면 중복 방어가 더이상 안됨)
    private final long DEDUPLICATION_EXTRA_MS = 1_000L; //해당 메소드 처리가 종료되면 남은 초기 시간과 관계 없이 새로운 시간으로 중복 처리 방어함 (종료가 되었더라도 1초 정도는 중복 방어함)

    //기능의 특성상 @Controller 에는 적용이 어려워 보임, @RestController 에만 적용하는 것으로 처리
    @Pointcut(
            "@within(org.springframework.web.bind.annotation.RestController) && " +
                    "(" +
                    "@within(com.sptek._frameworkWebCore._annotation.Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod) || " +
                    "@annotation(com.sptek._frameworkWebCore._annotation.Enable_PreventDuplicateRequest_At_RestController_RestControllerMethod)" +
                    ")"
    )
    public void pointCut() {}

    @Around("pointCut()")
    public Object pointCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("1. around start");

        //log.debug("sessionAttributeAll : {}", RequestUtil.getSessionAttributesAll(SpringUtil.getRequest(),true));
        // GET 요청을 제외 하고 싶을때
        //HttpServletRequest currentRequest = SpringUtil.getRequest();
        //if ("GET".equalsIgnoreCase(currentRequest.getMethod())) {
        //    log.debug("duplicateRequestCheck : GET,  just pass through");
        //    return joinPoint.proceed();
        //}

        // 해당 메소드의 서명값, 식별자로 사용할 수 있다 (동일 메소드의 경우 항상 동일 값)
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
                Object result = joinPoint.proceed();
                log.debug("4. around after joinPoint.proceed()");
                return result;
                
            } finally {  //exception 상황에서도 반드시 expire Ms 업데이트 필요
                // 1차로 길게 잡아놓은 expireTime 을 짧게 조정함
                long newExpireTime = System.currentTimeMillis() + DEDUPLICATION_EXTRA_MS;
                SpringUtil.getRequest().getSession(true).setAttribute(requestedMethodSignature, newExpireTime);
                log.debug("Request down, reset time (rest expireTime: {}).", newExpireTime);
            }
        }
    }

    private Object handleDuplicationForRestController() throws ServiceException {
        throw new ServiceException(ServiceErrorCodeEnum.DUPLICATION_REQUEST_ERROR);
    }

    // 좀더 쓰레드 세이프 하게 구성 하기 위해서 세션 에서 requestSignature 확인과 requestSignature 셋팅을 synchronized 방식 으로 수정함
    private boolean isDuplicationCase(String requestSignature) {
        HttpSession httpSession = SpringUtil.getRequest().getSession(true);
        String httpSessionId = httpSession.getId();
        ReentrantLock reentrantLock = SESSION_LOCK.computeIfAbsent(httpSessionId, k -> new ReentrantLock());

        try {
            reentrantLock.lock();
            Long requestSignatureExpireTime = (Long)httpSession.getAttribute(requestSignature);
            requestSignatureExpireTime = requestSignatureExpireTime == null ? 0L : requestSignatureExpireTime;

            if (requestSignatureExpireTime == 0L || requestSignatureExpireTime < System.currentTimeMillis()) {
                //실제 요청 작업이 길어질 경우 그 사이 Duplication 요청이 들어 올수 있기 때문에 1차적으로 긴 시간을 설정
                long newExpireTime = System.currentTimeMillis() + DEDUPLICATION_DEFAULT_MS;
                SpringUtil.getRequest().getSession(true).setAttribute(requestSignature, newExpireTime);
                log.debug("Accepted, it's not duplication request (rest expireTime: {}).", newExpireTime-System.currentTimeMillis());
                return false;
            } else {
                log.debug("Reject, it's duplication request (rest expireTime: {}).", requestSignatureExpireTime-System.currentTimeMillis());
                return true;
            }
        } finally {
            reentrantLock.unlock();
            // 대기자가 없으면 맵에서 제거해 메모리 관리
            if (!reentrantLock.hasQueuedThreads() && reentrantLock.getHoldCount() == 0) {
                SESSION_LOCK.remove(httpSessionId, reentrantLock);
            }
        }
    }

    @Before("pointCut()")
    public void pointCutBefore(JoinPoint joinPoint) {
        log.debug("2. before (다른 AOP 의 Around 시작, 해당 AOP 의 모든 처리를 다 끝내고 다시 복귀)");
        //to do what you need.
    }

    @After("pointCut()")
    public void pointCutAfter(JoinPoint joinPoint) {
        log.debug("3. after");
        //to do what you need.
    }
}