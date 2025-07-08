package com.sptek._projectCommon.commonObject.code;

import com.sptek._frameworkWebCore.base.code.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ServiceErrorCodeEnum implements BaseCode {
    //시스템 내부에서 발생하는 에러가 아니라 서비스 로직상 에러로 취급되야 하는 경우에 활용하는 Exception 이다. 서비스에 맞게 추가해 나가면 된다.
    //API에서 주로 활용되지만 viewController에서도 활용될수 있다. viewController에서 활용되는 경우 HttpStatus는 의미가 없음

    //example
    NO_RESOURCE_ERROR(HttpStatus.NOT_FOUND, "SE404", "해당 데이터가 없습니다."),
    ALREADY_EXIST_RESOURCE_ERROR(HttpStatus.CONFLICT, "SE409", "해당 데이터 가 이미 존재 합니다."),
    DUPLICATION_REQUEST_ERROR(HttpStatus.TOO_MANY_REQUESTS, "SE429", "Duplication Request Exception"), //동일한 request가 빠르게 연속으로 들어오는 경우 내부적으로 한번만 처리하기 위한 기능을 위해 만듬, (매우 예외적인 케이스의 에러코드임)
    PAYLOAD_TOO_LARGE_ERROR(HttpStatus.PAYLOAD_TOO_LARGE, "SE413", "Multipart File MaxUploadSizeExceeded Exception"),

    FILE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "SE101", "File Upload Exception"),

    DEFAULT_ERROR(HttpStatus.BAD_REQUEST, "SE999", "요청이 정상적으로 처리되지 않았습니다.");

    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;
}
