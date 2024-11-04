package com.sptek.webfw.config.springSecurity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AuthorityEnum implements BaseAuthority{
    AUTH_RETRIEVE_USER_ALL_FOR_MARKETING("R001", "RUAFM", "모든 User에 대해서 마케팅에 필요한 정보를 조회할 수 있는 권한", ""),
    AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY("R002", "RUAFD", "모든 User에 대해서 배송에 필요한 정보를 조회할 수 있는 권한", "");

    private String code;
    private String alias;
    private String desc;
    private String status;

    public static AuthorityEnum getRoleFromAuthCode(String code) {
        return Arrays.stream(values())
                .filter(roleEnum -> roleEnum.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make RoleEnum from value. Unknown RoleEnum value: " + code));
    }

    public static AuthorityEnum getRoleFromAuthTag(String alias) {
        return Arrays.stream(values())
                .filter(roleEnum -> roleEnum.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make RoleEnum from value. Unknown RoleEnum value: " + alias));
    }

    public static AuthorityEnum getRoleEnumFromName(String desc) {
        return Arrays.stream(values())
                .filter(roleEnum -> roleEnum.getDesc().equals(desc))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make RoleEnum from name. Unknown RoleEnum name: " + desc));
    }

    static void main(String[] args) {
        System.out.println("d");
    }
}
