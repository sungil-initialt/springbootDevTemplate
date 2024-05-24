package com.sptek.webfw.common.exceptionHandler;

import com.sptek.webfw.common.exception.DuplicatedRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@Slf4j
@ControllerAdvice
public class WebGlobalExceptionHandler {

    /*
    ExceptionHandler 의 Exception 타입을 최상위 타입인 Exception으로 설정하였기 때문에 viewController 이하에서 발생하는 모든 Exception은 이곳 한곳에서 처리된다.
    만약 해당 핸들러가 없다면 application.yml에 설정한 spring 에러 설정에 따라서 404에러 페이지가 호출되는 메커니즘 그대로 5xx.html 이 처리하게 됨
    (반대로 해당 핸들러가 있기때문에 5xx.html은 controller 이상의 영역에서 발생하는 에러가 있을때만 보여질 것임)
    개발시에는 해당 핸들러를 막아서 5xx.html로 유도하게 하면 좀더 에러 분석이 좋을 수 있음
    todo: viewController에서 발생되는 에러의 경우 사용자에게 공통된 에러 페이지를 보여주는것 외에 딱히 다른 처리가 있을수 있을까? 고민필요.
     */

    //어전 request 응답하기 전 동일한 request 중복 요청했을때 DuplicateRequestPreventAspect 에서 발생시킴
    @ExceptionHandler(DuplicatedRequestException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public void handleDuplicatedRequestException(Exception ex) {
        log.error(ex.getMessage());
        //web 핸들러임에도 에러가 발생되는 케이스의 특성상 응답코드만 내리고 page는 내리지 않음
    }

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
