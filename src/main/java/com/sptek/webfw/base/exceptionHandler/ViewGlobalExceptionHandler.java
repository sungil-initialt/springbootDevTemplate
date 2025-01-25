package com.sptek.webfw.base.exceptionHandler;

import com.sptek.webfw.annotation.EnableViewCommonErrorResponse;
import com.sptek.webfw.base.constant.CommonConstants;
import com.sptek.webfw.base.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


//@Profile(value = { "notused" })
@Slf4j
@ControllerAdvice(annotations = EnableViewCommonErrorResponse.class)
public class ViewGlobalExceptionHandler {
    // todo: viewController에서 발생되는 에러의 경우 사용자에게 공통된 에러 페이지를 보여주는것 외에 딱히 다른 처리가 있을수 있을까? 그래서 현재는 httpsttus 코드도 상세히 분리하고 있지않음, 고민필요.

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //개발자가 의도적으로 생성한 Exception는 ServiceException로 생성하며 해당 핸들러에서 처리 됨
    //ServiceException의 경우도 공통 에러페이지로 처리하도록 구성했으나 필요시 커스텀을 위해 구분해 놓음
    public Object handleServiceException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        return handleError(request, ex, "error/commonInternalErrorView");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //기타 모든 에러를 하나로 처리함 (view 에러 에서는 특별히 공통 에러 페이지 외 구분할 필요가 없기 때문에 한번에 처리함, 에러 종류별 구분된 에러 페이지가 필요하면 추가해 나갈 것)
    public Object handleUnexpectedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        return handleError(request, ex, "error/commonInternalErrorView");
    }

    private Object handleError(HttpServletRequest request, Exception ex, String viewName) {
        //view 요청에서 발생한 에러의 경우 이후에 구체적으로 어떤 에러가 발생했는지 정확히 알수 없기 때문에 저장해서 사용함.
        request.setAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE, ex.getMessage());
        return viewName;
        //return "error/XXX" // spring 호출 페이지와 통일할 수 도 있음
    }
}
