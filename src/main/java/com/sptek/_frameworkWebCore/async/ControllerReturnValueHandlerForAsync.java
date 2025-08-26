package com.sptek._frameworkWebCore.async;

import com.sptek._frameworkWebCore._annotation.Enable_AsyncResponse_At_RestControllerMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Component
public class ControllerReturnValueHandlerForAsync implements HandlerMethodReturnValueHandler {

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        var method = returnType.getMethod();
        return method != null
                && method.isAnnotationPresent(Enable_AsyncResponse_At_RestControllerMethod.class)
                && returnType.getParameterType() == Object.class;
    }

    @Override
    public void handleReturnValue(
            Object returnValue,
            MethodParameter returnType,
            ModelAndViewContainer mav,
            NativeWebRequest webRequest) throws Exception {

        if (returnValue instanceof CompletableFuture<?> cf) {
            DeferredResult<Object> dr = new DeferredResult<>();
            cf.whenComplete((v, ex) -> {
                if (ex != null) {
                    Throwable cause = (ex instanceof CompletionException && ex.getCause() != null)
                            ? ex.getCause() : ex;
                    dr.setErrorResult(cause);
                } else {
                    dr.setResult(v);
                }
            });
            mav.setRequestHandled(false);
            WebAsyncUtils.getAsyncManager(webRequest)
                    .startDeferredResultProcessing(dr, mav);
            return;
        }

        if (returnValue instanceof Callable<?> callable) {
            mav.setRequestHandled(false);
            WebAsyncUtils.getAsyncManager(webRequest)
                    .startCallableProcessing(new WebAsyncTask<>(callable), mav);
            return;
        }

        if (returnValue instanceof DeferredResult<?> dr) {
            mav.setRequestHandled(false);
            WebAsyncUtils.getAsyncManager(webRequest)
                    .startDeferredResultProcessing(dr, mav);
            return;
        }

        // todo: support 조건에는 맞지만 리턴 object 의 instance 조건이 맞지 않는 경우 기존 다른 체인으로 넘길까? 에러로 처리할까??
        // mav.setRequestHandled(false); // 다른 기본 체인에 맡김
        throw new IllegalStateException( // 에러로 처리
                "return Object instance is not match : " + (returnValue == null ? "null" : returnValue.getClass().getName())
        );
    }
}
