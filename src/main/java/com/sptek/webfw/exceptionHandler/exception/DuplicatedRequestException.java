package com.sptek.webfw.exceptionHandler.exception;

import com.sptek.webfw.code.ErrorCode;
import com.sptek.webfw.util.ReqResUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;

public class DuplicatedRequestException extends RuntimeException {

    @Getter
    private final ErrorCode errorCode;

    @Builder
    public DuplicatedRequestException(HttpServletRequest request) {
        super("occur duplicate request : " + ReqResUtil.getRequestUrlString(request));
        this.errorCode = ErrorCode.SERVICE_DUPLICATION_REQUEST_ERROR;
    }
}