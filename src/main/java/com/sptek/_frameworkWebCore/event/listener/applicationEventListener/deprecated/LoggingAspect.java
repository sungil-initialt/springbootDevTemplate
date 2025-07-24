package com.sptek._frameworkWebCore.event.listener.applicationEventListener.deprecated;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;

@Slf4j
//@Aspect
//@Component
public class LoggingAspect {
//
//
//    private static final Map<String, Boolean> methodUsageMap = new ConcurrentHashMap<>();
//    private static final String LOG_FILE = "method_usage_log.txt";
//
//    // 특정 패키지(com.example) 하위의 메서드만 추적
//    //@Before("execution(* com.sptek..*(..)) && !execution(* com.sptek._frameworkWebCore.config.filter..*(..))")
//    public Object trackMethodUsage(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
//        methodUsageMap.put(methodName, true);
//
//        return joinPoint.proceed();
//    }
//
//    //@PreDestroy
//    public void saveMethodUsageLog() {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
//            for (Map.Entry<String, Boolean> entry : methodUsageMap.entrySet()) {
//                writer.write(entry.getKey() + "=" + entry.getValue());
//                writer.newLine();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Map<String, Boolean> getMethodUsageMap() {
//        return methodUsageMap;
//    }



//    private static final ThreadLocal<List<String>> callStack = ThreadLocal.withInitial(ArrayList::new);
//    private static final String TARGET_PACKAGE_PREFIX = "com.sptek";
//
//    @After("execution(* com.sptek..*(..)) && !execution(* com.sptek._frameworkWebCore.config.filter..*(..)) && !execution(* com.sptek._projectCommon.filter..*(..))")
//    public Object trackMethodUsage(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
//
//        if (methodName.startsWith(TARGET_PACKAGE_PREFIX)) {
//            callStack.get().add(methodName);
//        }
//
//        try {
//            return joinPoint.proceed();
//        } finally {
//            // Remove method from call stack after execution
//            callStack.get().remove(methodName);
//        }
//    }
//
//    public static List<String> getCallStack() {
//        return new ArrayList<>(callStack.get());
//    }
//
//    public static void clearCallStack() {
//        callStack.get().clear();
//    }

    @After("execution(* com.sptek..*(..)) && !within(com.sptek._frameworkWebCore.config.filter..*) && !within(com.sptek._projectCommon.filter..*)")
    public void logMethodCall(JoinPoint joinPoint) {
        // Get the method signature
        String methodName = joinPoint.getSignature().toShortString();

        // Log the method name
        log.debug("Method called: " + methodName);
    }
}