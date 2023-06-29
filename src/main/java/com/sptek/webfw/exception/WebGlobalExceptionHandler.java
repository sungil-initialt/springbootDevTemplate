package com.sptek.webfw.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class WebGlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected final String handleAllExceptions(Exception ex) {
        log.error("Exception", ex);

        return "error/errorForWebControllerAdvice";
    }
}
