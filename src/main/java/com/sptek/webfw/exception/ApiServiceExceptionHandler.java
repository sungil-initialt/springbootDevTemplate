package com.sptek.webfw.exception;

import com.sptek.webfw.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
서비스 영역의 Exception 처리를 담당함 (ex: id/pw가 일치하지 않습니다, 구매 내역이 없습니다. 등)
 */
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ApiServiceExceptionHandler {

    @ExceptionHandler(ApiServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomException(ApiServiceException ex) {
        log.error("ApiServiceExceptionHandler", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ex.getApiErrorCode(), ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ex.getApiErrorCode().getHttpStatusCode());
    }
}
