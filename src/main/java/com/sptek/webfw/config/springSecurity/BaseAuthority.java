package com.sptek.webfw.config.springSecurity;

import org.springframework.http.HttpStatus;

public interface BaseAuthority {
    String getCode();
    String getAlias();
    String getDesc();
    String getStatus();
}
