package com.sptek.webfw.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sptek.webfw.code.ApiErrorCode;
import com.sptek.webfw.dto.ApiErrorResponse;
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
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ApiGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.NOT_VALID_ERROR, ex.getMessage(), ex.getBindingResult());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.NOT_VALID_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ApiErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("MissingRequestHeaderException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.REQUEST_BODY_MISSING_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.REQUEST_BODY_MISSING_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ApiErrorResponse> handleMissingRequestHeaderExceptionException(MissingServletRequestParameterException ex) {
        log.error("handleMissingServletRequestParameterException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.MISSING_REQUEST_PARAMETER_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity<ApiErrorResponse> handleBadRequestException(HttpClientErrorException ex) {
        log.error("HttpClientErrorException.BadRequest", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.BAD_REQUEST_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.BAD_REQUEST_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ApiErrorResponse> handleNoHandlerFoundExceptionException(NoHandlerFoundException ex) {
        log.error("handleNoHandlerFoundExceptionException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.NOT_FOUND_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.NOT_FOUND_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ApiErrorResponse> handleNullPointerException(NullPointerException ex) {
        log.error("handleNullPointerException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.NULL_POINT_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.NULL_POINT_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ApiErrorResponse> handleIOException(IOException ex) {
        log.error("handleIOException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.IO_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.IO_ERROR.getHttpStatusCode());
    }

    /*
    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<ApiErrorResponse> handleJsonParseExceptionException(JsonParseException ex) {
        log.error("handleJsonParseExceptionException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ErrorCode.JSON_PARSE_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ErrorCode.JSON_PARSE_ERROR.getHttpStatusCode());
    }
     */

    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<ApiErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("handleJsonProcessingException", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.REQUEST_BODY_MISSING_ERROR.getHttpStatusCode());
    }

    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Exception", ex);

        final ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(ApiErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(apiErrorResponse, ApiErrorCode.INTERNAL_SERVER_ERROR.getHttpStatusCode());
    }
}
