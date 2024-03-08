package com.sptek.webfw.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

//Rest API 요청이 성공일 경우 활용되는 성공코드 enum 객체
//http 상태코드로 사용되는  HttpStatus와, responseBody에 들어가는 resultCode, resultMessage 로 구성됨
@Getter
public enum SuccessCode {
    DEFAULT_SUCCESS(HttpStatus.OK, "S000", "Success"),
    SELECT_SUCCESS(HttpStatus.OK, "S001", "Select Success"),
    DELETE_SUCCESS(HttpStatus.OK, "S002", "Delete Success"),
    INSERT_SUCCESS(HttpStatus.OK, "S003", "Insert Success"),
    UPDATE_SUCCESS(HttpStatus.OK, "S004", "Update Success");


    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;


    SuccessCode(final HttpStatus httpStatusCode, final String resultCode, final String resultMessage) {
        this.httpStatusCode = httpStatusCode;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
