package com.sptek._frameworkWebCore.aspect;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncResponse_At_RestControllerMethod;
import com.sptek._frameworkWebCore.base.apiResponseDto.ApiCommonSuccessResponseDto;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Aspect
@Order(1000)
@Component

// fw 의 api Controller 가 object 타입으로 넘긴 결과를 적절한 타입으로 변경하여 리턴 처리
// todo: 중요 !!
//  CompletableFuture 처리가 추가 되면서 Async 응답을 하는 경우 쓰레드 내부에서는 joinPoint.proceed()를 사용 할수가 없음
//  그래서 ReflectionUtils 을 통해 직접 해당 메소드를 호출하여 처리하고 있음
//  이 경우 joinPoint.proceed() 가 호출 되지 않았음으로 본인의 befroe, after 및 AOP 필터 체인으로도 전파되지 못함
//  그래서 해당 AOP는 AOP 필터 체인에서 가장 하위에 존재해야 정상 동작 할수 있음을 알고 사용해야 함!

public class ApiCommonResponseHelperAspect {
    private final TaskExecutor taskExecutor;
    // Bean name 으로 명확히 찾기 위해 생성자 직접 구성
    public ApiCommonResponseHelperAspect(@Qualifier("taskExecutor") TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Pointcut(
            "@within(org.springframework.web.bind.annotation.RestController) && " +
                    "(" +
                    "@within(com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController) || " +
                    "@annotation(com.sptek._frameworkWebCore._annotation.Enable_ResponseOfApiCommonSuccess_At_RestController)" +
                    ")"
    )
    public void pointCut() {}

    @Around("pointCut()")
    public Object pointCutAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("1. around start");

        if (RequestMappingAnnotationRegister.hasAnnotation(SpringUtil.getRequest(), Enable_AsyncResponse_At_RestControllerMethod.class)) {
            return CompletableFuture.supplyAsync(() -> {
                Object target = joinPoint.getTarget();
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                // 프록시일 수 있으니 실제 구현 메서드로 보정
                method = org.springframework.aop.support.AopUtils.getMostSpecificMethod(method, target.getClass());
                Object[] args = joinPoint.getArgs();

                try {
                    //Object result = joinPoint.proceed(); // 쓰레드 내부에서 이렇게 처리할수 없음
                    Object result = org.springframework.util.ReflectionUtils.invokeMethod(method, target, args);
                    return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(result));
                } catch (Throwable t) {
                    throw new CompletionException(t);
                }
            }, taskExecutor);

        } else {
            Object result = joinPoint.proceed();
            log.debug("4. around after joinPoint.proceed()");
            if (result instanceof HttpEntity) { //ResponseEntity 포함
                // 이미 HttpEntity 또는 ResponseEntity 라면 수정 없이 그대로 반환
                return result;
            } else if (result instanceof CompletableFuture<?> completableFuture) {
                // 직접 CompletableFuture 로 만들어 넘긴 경우 ApiCommonSuccessResponseDto -> ResponseEntity -> CompletableFuture로 래핑
                return completableFuture.thenApply(obj -> ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(obj)));
            } else {
                return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(result));
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