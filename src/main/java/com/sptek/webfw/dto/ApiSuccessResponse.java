package com.sptek.webfw.dto;

import com.sptek.webfw.code.ApiSuccessCode;
import lombok.Getter;

@Getter
public class ApiSuccessResponse<T> {

    private String resultCode;
    private String resultMessage;
    private T result;

    public ApiSuccessResponse(final String resultCode, final String resultMessage, final T result) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.result = result;
    }

    public ApiSuccessResponse(final ApiSuccessCode apiSuccessCode, final T result) {
        this.resultCode = apiSuccessCode.getResultCode();
        this.resultMessage = apiSuccessCode.getResultMessage();
        this.result = result;
    }
}