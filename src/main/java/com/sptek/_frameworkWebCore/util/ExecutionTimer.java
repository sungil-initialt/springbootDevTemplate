package com.sptek._frameworkWebCore.util;

import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

@Slf4j
public class ExecutionTimer {
    private ExecutionTimer() {}

    public static void measure(String logTag, Runnable task) {
        long start = System.nanoTime();
        try {
            task.run();
        } finally {
            long end = System.nanoTime();
            log.info("■ {} took {} ms", logTag, (end - start) / 1_000_000.0);
        }
    }

    public static <T> T measure(String logTag, Supplier<T> task) {
        long start = System.nanoTime();
        try {
            return task.get();
        } finally {
            long end = System.nanoTime();
            log.info("■ {} took {} ms", logTag, (end - start) / 1_000_000.0);
        }
    }
}
