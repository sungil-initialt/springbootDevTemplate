package com.sptek.webfw.exception;

import com.sptek.webfw.code.ApiErrorCode;
import lombok.Builder;
import lombok.Getter;

public class ApiBusinessException extends RuntimeException {

    @Getter
    private final ApiErrorCode apiErrorCode;

    @Builder
    public ApiBusinessException(ApiErrorCode apiErrorCode, String exceptionMessage) {
        super(exceptionMessage);
        this.apiErrorCode = apiErrorCode;
    }

    @Builder
    public ApiBusinessException(ApiErrorCode apiErrorCode) {
        super();
        this.apiErrorCode = apiErrorCode;
    }
}