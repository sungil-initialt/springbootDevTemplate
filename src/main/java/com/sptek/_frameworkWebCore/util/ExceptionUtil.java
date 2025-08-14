package com.sptek._frameworkWebCore.util;

import lombok.extern.slf4j.Slf4j;

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

}
