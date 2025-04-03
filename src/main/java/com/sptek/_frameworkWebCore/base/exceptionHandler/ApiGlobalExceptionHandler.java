package com.sptek._frameworkWebCore.base.exceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sptek._frameworkWebCore.annotation.EnableResponseOfApiGlobalException_InRestController;
import com.sptek._frameworkWebCore.base.code.CommonErrorCodeEnum;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.base.apiResponseDto.ApiCommonErrorResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.IOException;
/*
RestController 의 GlobalException 처리를 담당함 (실행중 예상하지 않은 Exception에 대한 처리로 ServiceException과 비교할 수 있음)
Exception의 종류에 따라 에러코드와 Exception 메시지가 정해진다. (Exception 메시지는 실제 발생한 Exception의 메시지를 사용한다.)
최종 Response 응답까지 처리해 준다.
 */
@Slf4j
@RestControllerAdvice(annotations = EnableResponseOfApiGlobalException_InRestController.class) // @EnableFwApiGrobalExceptionHandler 가 선언된 RestController 에서만 동작함 (정확히는  RestController 여부는 체크 안함)
public class ApiGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.NOT_VALID_ERROR, ex.getMessage(), ex.getBindingResult());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.NOT_VALID_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleMethodArgumentTypeMismatchException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.INVALID_TYPE_VALUE_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.INVALID_TYPE_VALUE_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleMissingRequestHeaderException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.NOT_VALID_HEADER_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.NOT_VALID_HEADER_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleHttpMessageNotReadableException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.REQUEST_BODY_NOT_READABLE_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.REQUEST_BODY_NOT_READABLE_ERROR .getHttpStatusCode());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleMissingServletRequestParameterException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.MISSING_REQUEST_PARAMETER_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleHttpClientErrorException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.BAD_REQUEST_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.BAD_REQUEST_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleNullPointerException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.NULL_POINT_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.NULL_POINT_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleIOException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.IO_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.IO_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleJsonParseException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.JSON_PARSE_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.JSON_PARSE_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleJsonProcessingException(Exception ex) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.JACKSON_PROCESS_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.JACKSON_PROCESS_ERROR.getHttpStatusCode());
    }

    //권한 오류는 ApplicationGlobalExceptionHandler 처리 이지만.. 필터가 아닌 컨트럴러에서 권한 체크를 하는 경우도 있음으로 이곳에도 필요하다.
    @ExceptionHandler({AccessDeniedException.class, HttpClientErrorException.Unauthorized.class})
    public Object handleAccessDeniedException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        log.error(ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.FORBIDDEN_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.FORBIDDEN_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(ServiceException.class)
    //개발자가 의도적으로 생성한 Exception는 ServiceException로 생성하며 해당 핸들러에서 처리 됨
    public ResponseEntity<ApiCommonErrorResponseDto> handleServiceException(ServiceException ex) {
        log.error("{}, {}, {}", ex.getServiceErrorCodeEnum().getResultCode(), ex.getServiceErrorCodeEnum().getResultMessage(), ex.getMessage());

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(ex.getServiceErrorCodeEnum(), ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, ex.getServiceErrorCodeEnum().getHttpStatusCode());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiCommonErrorResponseDto> handleUnExpectedException(Exception ex) {
        log.error("Unexpected exception occurred", ex);

        final ApiCommonErrorResponseDto apiCommonErrorResponseDto = ApiCommonErrorResponseDto.of(CommonErrorCodeEnum.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiCommonErrorResponseDto, CommonErrorCodeEnum.INTERNAL_SERVER_ERROR.getHttpStatusCode());
    }
}
