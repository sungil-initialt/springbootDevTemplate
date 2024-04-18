package com.sptek.webfw.config.springSecurity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private String value;

    //특정 value 값의 UserRole을 반환함.
    public static UserRole getUserRoleFromValue(String value) {
        for (UserRole role : values()) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("can not make UserRole from value. it's Unknown role value: " + value);
    }
}
