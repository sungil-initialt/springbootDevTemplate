package com.sptek._frameworkWebCore.base.exception;

public class ExceptionHelper {
    @FunctionalInterface
    public interface SupplierWithEx<T> {
        T get() throws Exception;
    }

    // Exception 발생시 Exception을 throw 하지 않고 default 값으로 반환하게 해준다.
    public static <T> T exSafe(SupplierWithEx<T> s, T defaultValue) {
        try {
            return s.get();
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
