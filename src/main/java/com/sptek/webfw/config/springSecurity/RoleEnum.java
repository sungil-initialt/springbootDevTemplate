package com.sptek.webfw.config.springSecurity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleEnum {
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private String value;

    //해당 value 값의 RoleEnum을 반환함.
    public static RoleEnum getRoleFromValue(String value) {
        for (RoleEnum roleEnum : values()) {
            if (roleEnum.getValue().equals(value)) {
                return roleEnum;
            }
        }
        throw new IllegalArgumentException("can not make RoleEnum from value. it's Unknown RoleEnum value: " + value);
    }

    //해당 name 값의 RoleEnum을 반환함.
    public static RoleEnum getRoleEnumFromName(String name) {
        for (RoleEnum roleEnum : values()) {
            if (roleEnum.name().equals(name)) {
                return roleEnum;
            }
        }
        throw new IllegalArgumentException("can not make RoleEnum from name. it's Unknown RoleEnum name: " + name);
    }
}
