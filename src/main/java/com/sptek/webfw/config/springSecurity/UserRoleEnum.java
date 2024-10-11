package com.sptek.webfw.config.springSecurity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRoleEnum {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String value;

    //해당 value 값의 UserRoleEnum을 반환함.
    public static UserRoleEnum getUserRoleFromValue(String value) {
        for (UserRoleEnum userRoleEnum : values()) {
            if (userRoleEnum.getValue().equals(value)) {
                return userRoleEnum;
            }
        }
        throw new IllegalArgumentException("can not make UserRoleEnum from value. it's Unknown userRoleEnum value: " + value);
    }

    //해당 name 값의 UserRoleEnum을 반환함.
    public static UserRoleEnum getUserRoleEnumFromName(String name) {
        for (UserRoleEnum userRoleEnum : values()) {
            if (userRoleEnum.name().equals(name)) {
                return userRoleEnum;
            }
        }
        throw new IllegalArgumentException("can not make UserRoleEnum from name. it's Unknown userRoleEnum name: " + name);
    }
}
