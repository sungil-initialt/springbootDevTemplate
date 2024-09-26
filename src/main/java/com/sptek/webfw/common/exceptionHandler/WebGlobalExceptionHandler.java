package com.sptek.webfw.common.exceptionHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

//@Profile(value = { "notused" })
@Slf4j
@ControllerAdvice
public class WebGlobalExceptionHandler {

    /*
    ExceptionHandler에  Exception(최상위) 타입을 포함했기 때문에 발생하는 모든 Exception은 이곳 한곳에서 처리된다.
    만약 해당 핸들러가 없다면 application.yml에 설정한 spring 에러 설정에 따라서 404.html, 5xx.html 등이 처리하게 됨
    (spring이 처리할수 없는 영역에서의 에러가 발생한다면 이 핸들러가 있더라도 was 에러가 표시될수도 있음)
    개발시에는 이 핸들러를 막아서 5xx.html로 유도하게 하면 좀더 에러 분석이 좋을 수 있음
    todo: viewController에서 발생되는 에러의 경우 사용자에게 공통된 에러 페이지를 보여주는것 외에 딱히 다른 처리가 있을수 있을까?
     그래서 현재는 httpsttus 코드도 상세히 분리하고 있지않음, 고민필요.
     */

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFoundException(Exception ex) {
        log.error(ex.getMessage());
        return "error/commonInternalErrorView";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnExpectedException(Exception ex) {
        log.error(ex.getMessage());
        return "error/commonInternalErrorView";
    }

}
