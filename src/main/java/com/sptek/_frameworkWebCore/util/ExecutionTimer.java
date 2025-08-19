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
        Boolean result = has_Enable_ExecutionTimer_At_Main;
        if (result != null) return result;

        synchronized (ExecutionTimer.class) {
            if (has_Enable_ExecutionTimer_At_Main == null) {
                has_Enable_ExecutionTimer_At_Main = MainClassAnnotationRegister.hasAnnotation(Enable_ExecutionTimer_At_Main.class);
            }
            return has_Enable_ExecutionTimer_At_Main;
        }
    }

    public static void measure(String logTag, Runnable runnable) {
        if (checkAnnotation()) {
            long start = System.nanoTime();
            try {
                runnable.run();
            } finally {
                long end = System.nanoTime();
                log.info(CommonConstants.FW_LOG_PREFIX + "{} took {} ms", logTag, (end - start) / 1_000_000.0);
            }
        } else {
            runnable.run();
        }
    }

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
