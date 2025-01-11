package com.sptek.webfw.config.aspect;

import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.common.responseDto.ApiSuccessResponseDto;
import com.sptek.webfw.util.RequestUtil;
import com.sptek.webfw.util.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Aspect
@Component
public class ApiCommonResponseAspect {
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) " +
            "&& (@within(com.sptek.webfw.anotation.AnoApiCommonResponse) || @annotation(com.sptek.webfw.anotation.AnoApiCommonResponse))")
    public void myPointCut() {}


    @Around("myPointCut()")
    public Object duplicateRequestCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        // 반환값이 이미 ResponseEntity라면 수정 없이 그대로 반환
        if (result instanceof ResponseEntity) {
            return result;
        }

        // 반환값을 ResponseEntity<ApiSuccessResponseDto<?>> 형태로 래핑
        return ResponseEntity.ok(new ApiSuccessResponseDto<>(result));
    }

    @Before("myPointCut()")
    public void beforeRequest(JoinPoint joinPoint) {
        log.debug("AOP order : beforeRequest");
        //to do what you need.
    }

    @After("myPointCut()")
    public void afterRequest(JoinPoint joinPoint) {
        log.debug("AOP order : afterRequest");
        //to do what you need.
    }
}