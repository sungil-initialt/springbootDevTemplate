package com.sptek.webfw.common.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatusCode();
    String getResultCode();
    String getResultMessage();
}
