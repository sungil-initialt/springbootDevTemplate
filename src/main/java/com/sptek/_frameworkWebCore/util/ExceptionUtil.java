package com.sptek._frameworkWebCore.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ExceptionUtil {
    @FunctionalInterface
    public interface SupplierWithEx<T> {
        T get();// throws Exception;
    }

    @FunctionalInterface
    public interface RunnableWithEx {
        void run();// throws Exception;
    }

    // Exception 발생시 Exception을 throw 하지 않고 default 값으로 반환하게 해준다.
    public static <T> T exSafe(SupplierWithEx<T> s, T defaultValue) {
        try {
            return s.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // 반환값 없는 경우
    public static void exSafe(RunnableWithEx r) {
        try {
            r.run();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // 래핑된 ex 를 받아 실제 ex를 찾아 준다. (Async 내부 ex 의 경우 ex가 래핑되는 케이스가 있음)
    public static Throwable getRealException(Throwable t) {
        if (t == null) return null;

        // 아래 EX 는 래핑된 경우가 많음
        while (t instanceof CompletionException || t instanceof ExecutionException || t instanceof UndeclaredThrowableException) {
            Throwable cause = t.getCause();
            if (cause == null || cause == t) {
                return t;
            }
            t = cause;
        }
        return t;
    }
}
