package com.sptek._frameworkWebCore.aspect;

import com.sptek._frameworkWebCore.base.apiResponseDto.ApiCommonSuccessResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
// fw 의 api Controller 가 object 타입으로 넘긴 결과를 ApiCommonSuccessResponseDto 형태로 변형하고, ResponseEntity 를 구성해 전송 하도록 처리.
public class ApiCommonResponseHelperAspect {
    @Pointcut(
            "@within(org.springframework.web.bind.annotation.RestController)" + "&& (@within(com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController) " +
                    "|| @annotation(com.sptek._frameworkWebCore.annotation.EnableResponseOfApiCommonSuccess_InRestController))"
    )
    public void myPointCut() {}


    @Around("myPointCut()")
    public Object wrapWithResponseEntity(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        // 반환값이 이미 ResponseEntity라면 수정 없이 그대로 반환
        if (result instanceof ResponseEntity) {
            return result;
        }

        // 반환값을 ResponseEntity<ApiCommonSuccessResponseDto<?>> 형태로 래핑
        return ResponseEntity.ok(new ApiCommonSuccessResponseDto<>(result));
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