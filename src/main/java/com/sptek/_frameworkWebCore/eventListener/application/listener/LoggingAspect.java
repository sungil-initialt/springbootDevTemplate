package com.sptek._frameworkWebCore.eventListener.application.listener;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class LoggingAspect {


    private static final Map<String, Boolean> methodUsageMap = new ConcurrentHashMap<>();
    private static final String LOG_FILE = "method_usage_log.txt";

    // 특정 패키지(com.example) 하위의 메서드만 추적
    @Before("execution(* com.sptek..*(..)) && !execution(* com.sptek._frameworkWebCore.config.filter..*(..))")
    public Object trackMethodUsage(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        methodUsageMap.put(methodName, true);

        return joinPoint.proceed();
    }

    @PreDestroy
    public void saveMethodUsageLog() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
            for (Map.Entry<String, Boolean> entry : methodUsageMap.entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Boolean> getMethodUsageMap() {
        return methodUsageMap;
    }
}