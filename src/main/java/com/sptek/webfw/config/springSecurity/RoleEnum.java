package com.sptek.webfw.config.springSecurity;

import com.sptek.webfw.config.springSecurity.extras.dto.TermsDto;
import com.sptek.webfw.config.springSecurity.extras.entity.Terms;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum RoleEnum {
    ROLE_NOR_USER("ROLE_NOR_USER"),
    ROLE_VIP_USER("ROLE_VIP_USER"),
    ROLE_ADMIN_MARKETING("ROLE_ADMIN_MARKETING"),
    ROLE_ADMIN_DELIVERY("ROLE_ADMIN_DELIVERY");

    private String authorities;

    //해당 value 값의 RoleEnum을 반환함.
    public static RoleEnum getRoleFromAuthority(String authority) {
        return Arrays.stream(values())
                .filter(roleEnum -> roleEnum.getAuthorities().equals(authority))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make RoleEnum from value. Unknown RoleEnum value: " + authority));
    }


    //해당 name 값의 RoleEnum을 반환함.
    public static RoleEnum getRoleEnumFromName(String enumName) {
        return Arrays.stream(values())
                .filter(roleEnum -> roleEnum.name().equals(enumName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make RoleEnum from name. Unknown RoleEnum name: " + enumName));
    }
}
