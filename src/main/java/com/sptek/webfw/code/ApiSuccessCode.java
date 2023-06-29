package com.sptek.webfw.code;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiSuccessCode {
    DEFAULT_SUCCESS(HttpStatus.OK, "S000", "Success"),
    SELECT_SUCCESS(HttpStatus.OK, "S001", "Select Success"),
    DELETE_SUCCESS(HttpStatus.OK, "S002", "Delete Success"),
    INSERT_SUCCESS(HttpStatus.OK, "S003", "Insert Success"),
    UPDATE_SUCCESS(HttpStatus.OK, "S004", "Update Success");

    private final HttpStatus httpStatusCode;
    private final String resultCode;
    private final String resultMessage;

    ApiSuccessCode(final HttpStatus httpStatusCode, final String resultCode, final String resultMessage) {
        this.httpStatusCode = httpStatusCode;
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }
}
