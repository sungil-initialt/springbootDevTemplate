package com.sptek.webfw.base.exceptionHandler;

import com.sptek.webfw.anotation.RestControllerForSpecial;
import com.sptek.webfw.base.code.CommonErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(annotations = RestControllerForSpecial.class)
// @RestControllerForSpecial에 적용되야 하지만 @RestControllerForSpecial 내부에 @RestController가 있기 때문에
// @RestController 가 적용된 핸들러가 먼저 가져가는 현상이 남옴, 그래서 @Order 도 함께 올려줘야 함
public class ApiGlobalExceptionHandlerForRestControllerForSpecial {
    //원하는 Exception 처리를 계속 추가.

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exception(Exception ex) {
        // to do what you want.
        log.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), CommonErrorCodeEnum.INTERNAL_SERVER_ERROR.getHttpStatusCode());
    }
}
