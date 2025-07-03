package com.sptek._projectCommon.commonObject.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SecurePathTypeEnum {
    ANYONE("anyone"),
    LOGIN("login"),
    USER("user"),
    ROLE("role"),
    AUTH("auth");

    private final String pathName;
}
