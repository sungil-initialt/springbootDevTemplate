package com.sptek.webfw.exception;

import com.sptek.webfw.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/*
RestController 의 ServiceException 처리를 담당함
ApiServiceException 한 종류에 대한 처리만을 담당하고 api 응답을 받은쪽에서는 에러코드를 통해 적절한 처리를 함.
최종 Response 응답까지 처리해 준다.
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
