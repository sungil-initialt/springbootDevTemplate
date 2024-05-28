package com.sptek.webfw.common.exceptionHandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sptek.webfw.common.code.CommonErrorCodeEnum;
import com.sptek.webfw.common.exception.DuplicatedRequestException;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.common.responseDto.ApiErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
/*
RestController 의 GlobalException 처리를 담당함 (실행중 예상하지 않은 Exception에 대한 처리로 ServiceException과 비교할 수 있음)
Exception의 종류에 따라 에러코드와 Exception 메시지가 정해진다. (Exception 메시지는 실제 발생한 Exception의 메시지를 사용한다.)
최종 Response 응답까지 처리해 준다.
 */
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ApiGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.NOT_VALID_ERROR, ex.getMessage(), ex.getBindingResult());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.NOT_VALID_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponseDto> handleMethodArgumentTypeMismatchException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.INVALID_TYPE_VALUE_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.INVALID_TYPE_VALUE_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ApiErrorResponseDto> handleMissingRequestHeaderException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.NOT_VALID_HEADER_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.NOT_VALID_HEADER_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponseDto> handleHttpMessageNotReadableException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.REQUEST_BODY_NOT_READABLE_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.REQUEST_BODY_NOT_READABLE_ERROR .getHttpStatusCode());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponseDto> handleMissingServletRequestParameterException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.MISSING_REQUEST_PARAMETER_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<ApiErrorResponseDto> handleHttpClientErrorException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.BAD_REQUEST_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.BAD_REQUEST_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponseDto> handleNoHandlerFoundException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.NOT_FOUND_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.NOT_FOUND_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiErrorResponseDto> handleNullPointerException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.NULL_POINT_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.NULL_POINT_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiErrorResponseDto> handleIOException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.IO_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.IO_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ApiErrorResponseDto> handleJsonParseException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.JSON_PARSE_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.JSON_PARSE_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<ApiErrorResponseDto> handleJsonProcessingException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.JACKSON_PROCESS_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.JACKSON_PROCESS_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDto> handleUnExpectedException(Exception ex) {
        log.error(ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(CommonErrorCodeEnum.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, CommonErrorCodeEnum.INTERNAL_SERVER_ERROR.getHttpStatusCode());
    }



    //개발자가 의도적으로 생성하는 Exception는 ServiceException로 생성하며 해당 핸들러에서 처리 됨
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiErrorResponseDto> handleServiceException(ServiceException ex) {
        log.error("{}, {}, {}", ex.getServiceErrorCodeEnum().getResultCode(), ex.getServiceErrorCodeEnum().getResultMessage(), ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(ex.getServiceErrorCodeEnum(), ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, ex.getServiceErrorCodeEnum().getHttpStatusCode());
    }

    //어전 request 응답하기 전 동일한 request 중복 요청했을때 DuplicateRequestPreventAspect 에서 발생시킴
    @ExceptionHandler(DuplicatedRequestException.class)
    public ResponseEntity<ApiErrorResponseDto> handleDuplicatedRequestException(DuplicatedRequestException ex) {
        log.error("{}, {}, {}", ex.getServiceErrorCodeEnum().getResultCode(), ex.getServiceErrorCodeEnum().getResultMessage(), ex.getMessage());

        final ApiErrorResponseDto apiErrorResponseDto = ApiErrorResponseDto.of(ex.getServiceErrorCodeEnum(), ex.getMessage());
        return new ResponseEntity<>(apiErrorResponseDto, ex.getServiceErrorCodeEnum().getHttpStatusCode());
    }
}
