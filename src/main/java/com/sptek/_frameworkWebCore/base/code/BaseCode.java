package com.sptek._frameworkWebCore.base.code;

import org.springframework.http.HttpStatus;

public interface BaseCode {
    HttpStatus getHttpStatusCode();
    String getResultCode();
    String getResultMessage();
}
