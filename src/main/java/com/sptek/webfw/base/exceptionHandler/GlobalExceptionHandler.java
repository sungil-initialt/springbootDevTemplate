package com.sptek.webfw.base.exceptionHandler;

import com.sptek.webfw.anotation.EnableFwViewGrobalExceptionHandler;
import com.sptek.webfw.base.code.CommonErrorCodeEnum;
import com.sptek.webfw.base.constant.CommonConstants;
import com.sptek.webfw.base.responseDto.ApiErrorResponseDto;
import com.sptek.webfw.util.RequestUtil;
import com.sptek.webfw.util.TypeConvertUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.util.Optional;

/*
view Controller 에서 발생한 Exception 의 처리가 주? 목적이지만 컨트롤러 영역 밖에서 발생한 에러도 이곳에서 처리 됨으로 API 요청에 대한 에러처리 기능도 포함하고 있음
만약 해당 핸들러가 없다면 application.yml에 설정한 spring 에러 설정에 따라서 404.html, 5xx.html 등이 처리하게 됨
(spring이 처리할수 없는 영역에서의 에러가 발생한다면 이 핸들러가 있더라도 was 에러가 표시될수도 있음 -> CustomErrorController 를 만들어 이 케이스도 직접 처리함
개발시에는 이 핸들러를 막아서 5xx.html로 유도하게 하면 좀더 에러 분석이 좋을 수 있음
todo: viewController에서 발생되는 에러의 경우 사용자에게 공통된 에러 페이지를 보여주는것 외에 딱히 다른 처리가 있을수 있을까? 그래서 현재는 httpsttus 코드도 상세히 분리하고 있지않음, 고민필요.
*/

//@Profile(value = { "notused" })
@Slf4j
@ControllerAdvice(annotations = EnableFwViewGrobalExceptionHandler.class)
public class GlobalExceptionHandler {

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    //요청에 대한 url 매핑 자체가 없기 때문에 ExceptionHandler 의 web/api 구분없이 이쪽(web쪽)으로 들어옴
    public Object handleNoResourceFoundException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        logWithCondition(ex, request, response, HttpStatus.NOT_FOUND);
        return handleError(request, response, ex, CommonErrorCodeEnum.NOT_FOUND_ERROR, "error/commonNotfoundErrorView");
    }

    @ExceptionHandler({AccessDeniedException.class, HttpClientErrorException.Unauthorized.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    //controller 에서 hasRole 이든 hasAuthority 든 AccessDeniedException 이 발생됨 (hasRole인 경우는 401 같지는 403이 나옴)
    public Object handleAccessDeniedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        // todo : view 요청에서 로그인이 안된상태여서 권한 에러가 났을때는 에러 페이지 보단 로그인 페이지로 더 친절히 이동해 줄까?
        logWithCondition(ex, request, response, HttpStatus.FORBIDDEN);
        return handleError(request, response, ex, CommonErrorCodeEnum.FORBIDDEN_ERROR, "error/commonAuthenticationErrorView");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class) // 지원하지 않는 request Metho(GET, POST, PUT, DELETE...)로 요청 했을때
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Object handleHttpRequestMethodNotSupportedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        logWithCondition(ex, request, response, HttpStatus.METHOD_NOT_ALLOWED);
        return handleError(request, response, ex, CommonErrorCodeEnum.METHOD_NOT_ALLOWED, "error/commonInternalErrorView");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //view 쪽 에러 처리는 거의 공통 에러 페이지로 이동뿐 다양한 처리를 할게 없음으로 ServiceException의 경우도 handleUnExpectedException 에서 함께 처리함
    public Object handleUnexpectedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        logWithCondition(ex, request, response, HttpStatus.INTERNAL_SERVER_ERROR);
        return handleError(request, response, ex, CommonErrorCodeEnum.INTERNAL_SERVER_ERROR, "error/commonInternalErrorView");
    }

    private void logWithCondition(Exception ex, HttpServletRequest request, HttpServletResponse response, HttpStatus httpStatus) {
        log.error("Exception message : {}", ex.getMessage());

        //  ReqResLogFilter 로 진입이 불가능한 케이스가 있기 때문에 이경우 이곳에서 요약된 로그를 남긴다.(ex: security 필터 같은 경우)
        // todo: 컨트롤러나 필터를 진입할 수 없는 케이스의 에러가 발생한 경우 항상 ERROR_REQUEST_URI 가 생성 되는 것으로 보이나 지속적 으로 살펴볼 필요 있음
        if (request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) != null
                // || ex instanceof xxxException // todo: 관련 다른 케이스가 확인 되면 추가 필요
        ) {
            String sessionId = request.getSession().getId();
            String methodType = RequestUtil.getRequestMethodType(request);
            String url = RequestUtil.getRequestDomain(request) +
                    Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).orElse(request.getRequestURI()) +
                    (StringUtils.hasText(request.getQueryString()) ? "?" + request.getQueryString() : "");

            String requestHeader = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(request, "|"));
            String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(request));

            log.debug("\n--------------------\n[ Occurred Higher-level Error ]\n" +
                            "session : {}\n" +
                            "({}) url : {}\n" +
                            "header : {}\n" +
                            "params : {}\n" +
                            "responseStatus : {}\n" +
                            "exceptionMsg : {}\n--------------------\n"
                    , sessionId
                    , methodType, url
                    , params
                    , requestHeader
                    , httpStatus
                    , ex.getMessage()
            );
        }
    }

    private Object handleError(HttpServletRequest request, HttpServletResponse response, Exception ex, CommonErrorCodeEnum commonErrorCodeEnum, String viewName) {
        String requestUri = request.getRequestURI();
        String errorRequestUri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                .map(Object::toString)
                .orElse("");

        if (requestUri.startsWith("/api/") || errorRequestUri.startsWith("/api/")) {
            ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(commonErrorCodeEnum, ex.getMessage());
            return new ResponseEntity<>(apiErrorResponseDto, commonErrorCodeEnum.getHttpStatusCode());

        } else {
            //view 요청에서 발생한 에러의 경우 이후에 구체적으로 어떤 에러가 발생했는지 정확히 알수 없기 때문에 저장해서 사용함.
            request.setAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE, ex.getMessage());
            return viewName;
            //return "error/XXX" // spring 호출 페이지와 통일할 수 도 있음
        }
    }
}
