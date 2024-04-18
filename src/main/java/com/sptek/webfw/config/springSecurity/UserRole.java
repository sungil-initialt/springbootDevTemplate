package com.sptek.webfw.config.springSecurity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String value;

    //해당 value 값의 UserRole을 반환함.
    public static UserRole getUserRoleFromValue(String value) {
        for (UserRole userRole : values()) {
            if (userRole.getValue().equals(value)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("can not make UserRole from value. it's Unknown userRole value: " + value);
    }

    //해당 name 값의 UserRole을 반환함.
    public static UserRole getUserRoleFromName(String name) {
        for (UserRole userRole : values()) {
            if (userRole.name().equals(name)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("can not make UserRole from name. it's Unknown userRole name: " + name);
    }
}
