package com.sptek.webfw.common.exception;

import com.sptek.webfw.common.code.ErrorCodeEnum;
import com.sptek.webfw.util.ReqResUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

public class DuplicatedRequestException extends RuntimeException {

    @Getter
    private final ErrorCodeEnum errorCodeEnum;

    public DuplicatedRequestException(HttpServletRequest request) {
        super("occur duplicate request : " + ReqResUtil.getRequestUrlString(request));
        this.errorCodeEnum = ErrorCodeEnum.SERVICE_DUPLICATION_REQUEST_ERROR;
    }
}