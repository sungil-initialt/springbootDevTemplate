package com.sptek.webfw.common.exception;

import com.sptek.webfw.common.code.BaseCode;
import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.util.ReqResUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

public class DuplicatedRequestException extends RuntimeException {

    @Getter
    private final BaseCode serviceErrorCodeEnum;

    public DuplicatedRequestException(HttpServletRequest request) {
        super("occur duplicate request : " + ReqResUtil.getRequestUrlString(request));
        this.serviceErrorCodeEnum = ServiceErrorCodeEnum.SERVICE_DUPLICATION_REQUEST_ERROR;
    }
}