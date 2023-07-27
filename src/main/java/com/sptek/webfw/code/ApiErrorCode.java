package com.sptek.webfw.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiErrorCode {
    //범용적으로 사용되고 있는 httpstatus 와 관련된 에러 (httpstatuscode를 그에 맞게 내린다)
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "GE001", "Bad Request Exception"),
    REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "GE002", "Required request body is missing"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "GE003", " Invalid Type Value"),
    MISSING_REQUEST_PARAMETER_ERROR(HttpStatus.BAD_REQUEST, "GE004", "Missing Servlet RequestParameter Exception"),
    IO_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GE005", "I/O Exception"),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "GE006", "JsonParseException"),
    JACKSON_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "GE007", "com.fasterxml.jackson.core Exception"),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "GE008", "Forbidden Exception"),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "GE009", "Not Found Exception"),
    NULL_POINT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GE010", "Null Point Exception"),
    NOT_VALID_ERROR(HttpStatus.BAD_REQUEST, "GE011", "handle Validation Exception"),
    NOT_VALID_HEADER_ERROR(HttpStatus.BAD_REQUEST, "GE012", "Not Found Specific Header Exception"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "GE999", "Internal Server Error Exception"),



    //내부적으로 추가된 에러코드로 (에러 상황이지만 httpstatuscode는 200으로 내린다)
    BUSINESS_DEFAULT_ERROR(HttpStatus.OK, "BE000", "Default Error Exception"),
    BUSINESS_INSERT_ERROR(HttpStatus.OK, "BE001", "Insert Transaction Error Exception"),
    BUSINESS_UPDATE_ERROR(HttpStatus.OK, "BE002", "Update Transaction Error Exception"),
    BUSINESS_DELETE_ERROR(HttpStatus.OK, "BE003", "Delete Transaction Error Exception");


    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;

    ApiErrorCode(final HttpStatus httpStatusCode, final String resultCode, final String resultMessage) {
        this.httpStatusCode = httpStatusCode;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
