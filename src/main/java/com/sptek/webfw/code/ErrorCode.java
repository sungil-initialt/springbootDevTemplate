package com.sptek.webfw.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

//Rest API 요청이 실패일 경우 활용되는 실패코드 enum 객체
//http 상태코드로 사용되는  HttpStatus와, responseBody에 들어가는 resultCode, resultMessage 로 구성됨
@Getter
public enum ErrorCode {
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


    //시스템 내부적으로 활용되는 에러코드 예시로 서비스에 맞게 추가해 나가면 된다. (todo: 이 경우 HttpStatus가 200이 맞을까? 고민해 봐야함)
    SERVICE_DEFAULT_ERROR(HttpStatus.OK, "SE000", "Default Error Exception"),
    SERVICE_INSERT_ERROR(HttpStatus.OK, "SE001", "Insert Transaction Error Exception"),
    SERVICE_UPDATE_ERROR(HttpStatus.OK, "SE002", "Update Transaction Error Exception"),
    SERVICE_DELETE_ERROR(HttpStatus.OK, "SE003", "Delete Transaction Error Exception"),

    SERVICE_DUPLICATION_REQUEST_ERROR(HttpStatus.OK, "SE202", "Duplication Request Error Exception");


    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;


    ErrorCode(final HttpStatus httpStatusCode, final String resultCode, final String resultMessage) {
        this.httpStatusCode = httpStatusCode;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
