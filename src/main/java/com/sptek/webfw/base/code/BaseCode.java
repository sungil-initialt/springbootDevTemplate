package com.sptek.webfw.base.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatusCode();
    String getResultCode();
    String getResultMessage();
}
