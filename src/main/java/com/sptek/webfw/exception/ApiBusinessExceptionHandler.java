package com.sptek.webfw.exception;

import com.sptek.webfw.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ApiBusinessExceptionHandler {

    @ExceptionHandler(ApiBusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomException(ApiBusinessException ex) {
        log.error("BusinessExceptionHandler", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ex.getApiErrorCode(), ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ex.getApiErrorCode().getHttpStatusCode());
    }
}
