package com.sptek._frameworkWebCore.base.exceptionHandler;

import com.sptek._frameworkWebCore.annotation.EnableApplicationCommonErrorResponse;
import com.sptek._frameworkWebCore.base.code.CommonErrorCodeEnum;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.base.apiResponseDto.ApiCommonErrorResponseDto;
import com.sptek.serviceName._global.util.RequestUtil;
import com.sptek._frameworkWebCore.util.SptFwUtil;
import com.sptek.serviceName._global.util.TypeConvertUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
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

//@Profile(value = { "notused" })
@Slf4j
@ControllerAdvice
@Conditional(ApplicationGlobalExceptionHandler.ApplicationGlobalExceptionHandlerCondition.class)
public class ApplicationGlobalExceptionHandler {
    // todo: 여기서는 상위 레벨 에러 처리가 목적이지만, 사실상 view / api / 상위 레벨 에러 모두 여기서 처리 가능함.
    //  상세 처리를 위해서 view / api 분리해 놓은 것이지 view / api 에러 핸들러를 적용하지 않으면 이곳에서 모두 처리됨 (다만 resultCode, resultMessage 가 상세되지 못함)

    public ApplicationGlobalExceptionHandler() {
        log.info(CommonConstants.SERVER_INITIALIZATION_MARK + this.getClass().getSimpleName() + " is Applied");
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    //요청에 대한 url 매핑 자체가 없기 때문에 ApplicationGlobalExceptionHandler 로 들어옴
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

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    // 지원하지 않는 request Metho(GET, POST, PUT, DELETE...)로 요청 했을때
    public Object handleHttpRequestMethodNotSupportedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        logWithCondition(ex, request, response, HttpStatus.METHOD_NOT_ALLOWED);
        return handleError(request, response, ex, CommonErrorCodeEnum.METHOD_NOT_ALLOWED, "error/commonInternalErrorView");
    }

    //Exception 클레스를 처리하기 때문에 사실상 모든 에러 처리가 가능함
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    //그 외 예상할 수 없는 모든 에러 처리
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

            String logBody = String.format(
                      "session : %s\n"
                    + "(%s) url : %s\n"
                    + "header : %s\n"
                    + "params : %s\n"
                    + "responseStatus : %s\n"
                    + "exceptionMsg : %s\n"
                    , sessionId
                    , methodType, url
                    , params
                    , requestHeader
                    , httpStatus
                    , ex.getMessage()
            );
            log.info(SptFwUtil.convertSystemNotice("Application(High-level) Error occurred. caught by the ApplicationGlobalExceptionHandler", logBody));
        }
    }

    private Object handleError(HttpServletRequest request, HttpServletResponse response, Exception ex, CommonErrorCodeEnum commonErrorCodeEnum, String viewName) {
        String requestUri = request.getRequestURI();
        String errorRequestUri = Optional.ofNullable(request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI))
                .map(Object::toString)
                .orElse("");

        if (requestUri.startsWith("/api/") || errorRequestUri.startsWith("/api/")) {
            ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(commonErrorCodeEnum, ex.getMessage());
            return new ResponseEntity<>(apiCommonErrorResponseDto, commonErrorCodeEnum.getHttpStatusCode());

        } else {
            //view 요청에서 발생한 에러의 경우 이후에 구체적으로 어떤 에러가 발생했는지 정확히 알수 없기 때문에 저장해서 사용함.
            request.setAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE, ex.getMessage());
            return viewName;
            //return "error/XXX" // spring 호출 페이지와 통일할 수 도 있음
        }
    }


    public static class ApplicationGlobalExceptionHandlerCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {
            Environment environment = context.getEnvironment();
            String mainClassName = environment.getProperty("sun.java.command");
            //log.debug("mainClassName: {}", mainClassName);

            try {
                Class<?> mainClass = Class.forName(mainClassName);
                return mainClass.isAnnotationPresent(EnableApplicationCommonErrorResponse.class);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
    }
}
