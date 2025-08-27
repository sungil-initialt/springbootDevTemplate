package com.sptek._frameworkWebCore.aspect;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncResponse_At_RestControllerMethod;
import com.sptek._frameworkWebCore.base.apiResponseDto.ApiCommonSuccessResponseDto;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Aspect
@Component
// fw 의 api Controller 가 object 타입으로 넘긴 결과를 ApiCommonSuccessResponseDto 형태로 변형하고, ResponseEntity 를 구성해 전송 하도록 처리. (CompletableFuture 처리 추가)
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
        if (RequestMappingAnnotationRegister.hasAnnotation(SpringUtil.getRequest(), Enable_AsyncResponse_At_RestControllerMethod.class)) {
            return CompletableFuture.supplyAsync(() -> {
                Object target = joinPoint.getTarget();
                Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
                // 프록시일 수 있으니 실제 구현 메서드로 보정
                method = org.springframework.aop.support.AopUtils.getMostSpecificMethod(method, target.getClass());
                Object[] args = joinPoint.getArgs();

                try {
                    Object value = org.springframework.util.ReflectionUtils.invokeMethod(method, target, args);
                    return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(value));
                } catch (Throwable t) {
                    throw new CompletionException(t);
                }
            }, taskExecutor);

        } else {
            Object result = joinPoint.proceed();
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
}