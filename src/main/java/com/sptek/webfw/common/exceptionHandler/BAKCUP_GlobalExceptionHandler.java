package com.sptek.webfw.common.exceptionHandler;

import com.sptek.webfw.common.code.CommonErrorCodeEnum;
import com.sptek.webfw.common.constant.CommonConstants;
import com.sptek.webfw.common.responseDto.ApiErrorResponseDto;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Optional;

//@Profile(value = { "notused" })
@Slf4j
//@ControllerAdvice
public class BAKCUP_GlobalExceptionHandler {
    //view Controller 에서 발생한 Exception 의 처리가 주? 목적이지만 컨트롤러 영역 밖에서 발생한 에러도 이곳에서 처리 됨으로 API 요청에 대한 에러처리 기능도 포함하고 있음
    //만약 해당 핸들러가 없다면 application.yml에 설정한 spring 에러 설정에 따라서 404.html, 5xx.html 등이 처리하게 됨
    //(spring이 처리할수 없는 영역에서의 에러가 발생한다면 이 핸들러가 있더라도 was 에러가 표시될수도 있음)
    //개발시에는 이 핸들러를 막아서 5xx.html로 유도하게 하면 좀더 에러 분석이 좋을 수 있음
    //todo: viewController에서 발생되는 에러의 경우 사용자에게 공통된 에러 페이지를 보여주는것 외에 딱히 다른 처리가 있을수 있을까? 그래서 현재는 httpsttus 코드도 상세히 분리하고 있지않음, 고민필요.


    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    //요청에 대한 url 매핑이 없는 경우 web/api 구분없이 이쪽(web쪽)으로 들어옴 (매핑이 없기때문에 web/api 구분 자체가 불가능함)
    //api 입장에서는 api 규격과 다르게 페이지 컨텐츠가 내려가겠지만.. status 404를 통해 404의 경우 내용을 보지 말고 status 코드로 처리해야함, 분리해 볼수도 있겠지만 불필요해 보임
    public Object handleNoResourceFoundException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error(ex.getMessage());
        String requestUri = request.getRequestURI();
        String originUri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).map(Object::toString).orElse("");

        //originUri는 controller 영역 밖에서 에러가 발생한 경우에 셋팅됨 (ReqResLoggingInterceptor 가 동작할수 없기때문에 이곳에서 로깅함)
        if(StringUtils.hasText(originUri)) this.logReqResInfo(request, response, ex);

        if(requestUri.startsWith("/api/") || originUri.startsWith("/api/")) {
            final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.NOT_FOUND_ERROR, ex.getMessage());
            return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.NOT_FOUND_ERROR.getHttpStatusCode());

        } else {
            //view 에러의 경우 ReqResLoggingInterceptor 에서 구체적으로 어떤 에러가 발생했는지 정확히 알수 없기 때문에 저장해서 사용함.
            request.setAttribute(CommonConstants.REQ_PROPERTY_NAME_FOR_EXCEPTION_MESSAGE_LOGGING, ex.getMessage());
            return "error/commonNotfoundErrorView";
            //return "error/404"; // spring 호출과 통일할 경우
        }
    }

    //controller 에서 hasRole 이든 hasAuthority 든 AccessDeniedException 이 발생됨 (hasRole인 경우는 401 같지는 403이 나옴)
    @ExceptionHandler({AccessDeniedException.class, HttpClientErrorException.Unauthorized.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleAccessDeniedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error(ex.getMessage());
        //todo : 뭔가 더 친절히 처리해??
        //if(SecurityContextHolder.getContext().getAuthentication() != null
        //        && SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equalsIgnoreCase("anonymousUser")
        //) {
        //    //로그인 자체가 되어 있지 않다면 로그인 페이지로 이동해주게 처리 할까?
        //}

        String requestUri = request.getRequestURI();
        String originUri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).map(Object::toString).orElse("");

        //originUri는 controller 영역 밖에서 에러가 발생한 경우에 셋팅됨 (ReqResLoggingInterceptor 가 동작할수 없기때문에 이곳에서 로깅함)
        if(StringUtils.hasText(originUri)) this.logReqResInfo(request, response, ex);

        if(requestUri.startsWith("/api/") || originUri.startsWith("/api/")) {
            final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.FORBIDDEN_ERROR, ex.getMessage());
            return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.FORBIDDEN_ERROR.getHttpStatusCode());

        } else {
            //view 에러의 경우 ReqResLoggingInterceptor 에서 구체적으로 어떤 에러가 발생했는지 정확히 알수 없기 때문에 저장해서 사용함.
            request.setAttribute(CommonConstants.REQ_PROPERTY_NAME_FOR_EXCEPTION_MESSAGE_LOGGING, ex.getMessage());
            return "error/commonAuthenticationErrorView";
            //return "error/403"; // spring 호출과 통일할 경우
        }
    }

    //view 쪽 에러 컨트럴은 거의 공통 에러 페이지로 이동뿐 다양한 처리가 불가 함으로 ServiceException의 경우도 handleUnExpectedException 에서 함께 처리함
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleUnExpectedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error(ex.getMessage());
        String requestUri = request.getRequestURI();
        String originUri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)).map(Object::toString).orElse("");

        //originUri는 controller 영역 밖에서 에러가 발생한 경우에 셋팅됨 (ReqResLoggingInterceptor 가 동작할수 없기때문에 이곳에서 로깅함)
        if(StringUtils.hasText(originUri)) this.logReqResInfo(request, response, ex);

        if(requestUri.startsWith("/api/") || originUri.startsWith("/api/")) {
            final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.INTERNAL_SERVER_ERROR, ex.getMessage());
            return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.INTERNAL_SERVER_ERROR.getHttpStatusCode());

        } else {
            //view 에러의 경우 ReqResLoggingInterceptor 에서 구체적으로 어떤 에러가 발생했는지 정확히 알수 없기 때문에 저장해서 사용함.
            request.setAttribute(CommonConstants.REQ_PROPERTY_NAME_FOR_EXCEPTION_MESSAGE_LOGGING, ex.getMessage());
            return "error/commonInternalErrorView";
            //return "error/5xx"; // spring 호출과 통일할 경우
        }
    }



    //ReqResLoggingInterceptor 가 동작할 수 없는 상태에서 에러가 발생한 경우를 위해 (ex: security 필터 에러등)
    private void logReqResInfo(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        String session = request.getSession().getId();
        String methodType = RequestUtil.getRequestMethodType(request);
        String url = RequestUtil.getRequestDomain(request) + request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI) + (StringUtils.hasText(request.getQueryString()) ? "?" + request.getQueryString() : ""); //에러를 발생시킨 origin url
        String header = TypeConvertUtil.strMapToString(RequestUtil.getRequestHeaderMap(request));
        String params = TypeConvertUtil.strArrMapToString(RequestUtil.getRequestParameterMap(request));

        log.debug("\n--------------------\n[ReqRes Info from GlobalExceptionHandler]\nsession : {}\n({}) url : {}\nheader : {}\nparams : {}\nexceptionMsg : {}\n<-- responseStatus : {}\n--------------------\n"
                    , session, methodType, url, header, params, ex.getMessage(), response.getStatus());
    }
}
