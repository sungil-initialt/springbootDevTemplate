package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore._annotation.Enable_ExecutionTimer_At_Main;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class ExecutionTimer {
    private ExecutionTimer() {}
    private static volatile Boolean has_Enable_ExecutionTimer_At_Main = null;

    // 좀더 정확한 시간 측정을 위해 어노테이션 여부도 캐싱하여 사용함
    private static boolean checkAnnotation() {
        if (has_Enable_ExecutionTimer_At_Main != null) return has_Enable_ExecutionTimer_At_Main;
        return has_Enable_ExecutionTimer_At_Main = MainClassAnnotationRegister.hasAnnotation(Enable_ExecutionTimer_At_Main.class);
    }

    //return 없을때
    public static void measure(String logTag, Runnable runnable) {
        if (checkAnnotation()) {
            long start = System.nanoTime();
            try {
                // 참고! 호출자 동일 쓰레드에서 동작함 new Thread(runable).start() 와 다름
                runnable.run();
            } finally {
                long end = System.nanoTime();
                log.info(CommonConstants.FW_LOG_PREFIX + "{} took {} ms", logTag, (end - start) / 1_000_000.0);
            }
        } else {
            runnable.run();
        }
    }

    //return 있을때
    public static <T> T measure(String logTag, Supplier<T> supplier) {
        if (checkAnnotation()) {
            long start = System.nanoTime();
            try {
                return supplier.get();
            } finally {
                long end = System.nanoTime();
                log.info(CommonConstants.FW_LOG_PREFIX + "{} took {} ms", logTag, (end - start) / 1_000_000.0);
            }
        } else {
            return supplier.get();
        }
    }
}
