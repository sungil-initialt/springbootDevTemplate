package com.sptek.webfw.common.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

//Rest API 요청이 실패일 경우 활용되는 실패코드 enum 객체
//http 상태코드로 사용되는  HttpStatus와, responseBody에 들어가는 resultCode, resultMessage 로 구성됨
//에러 DTO를 통해 넘어갈때는 exceptionMessage가 추가되는데 exceptionMessage는 실제 에러메시지에로 개발자 정보 측면이라면
//ErrorCodeEnum의 resultMessage는 사용자 알림등의 활용에서 사용되는 용도로 보면 좋을것 같다(물론 result 코드를 이용해 처리할 수도 있음)
@Getter
public enum ErrorCodeEnum {
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




    //시스템 내부에서 발생하는 에러가 아니라 서비스 로직상 에러로 취급되야 하는 경우에 활용하는 Exception 이다. 서비스에 맞게 추가해 나가면 된다.
    //API 에서 주로 활용되지만 viewController에서도 활용될수 있다. viewController에서 활용되는 경우 HttpStatus는 의미가 없음
    //todo: 이 경우 HttpStatus가 200이 맞을까? 고민해 봐야함
    SERVICE_DEFAULT_ERROR(HttpStatus.OK, "SE000", "Default Error Exception"),


    //동일한 request가 빠르게 연속으로 들어오는 경우 내부적으로 한번만 처리하기 위한 기능을 위해 만듬, (매우 예외적인 케이스의 에러코드임)
    SERVICE_DUPLICATION_REQUEST_ERROR(HttpStatus.TOO_MANY_REQUESTS, "SE429", "Duplication Request Exception");



    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;

    ErrorCodeEnum(final HttpStatus httpStatusCode, final String resultCode, final String resultMessage) {
        this.httpStatusCode = httpStatusCode;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
