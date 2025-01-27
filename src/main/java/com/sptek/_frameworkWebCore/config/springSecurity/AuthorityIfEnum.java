package com.sptek._frameworkWebCore.config.springSecurity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum AuthorityIfEnum implements AuthorityIf {
    AUTH_SPECIAL_FOR_TEST("R000", "SFT", "테스트를 위해 만든 권한", ""),
    AUTH_RETRIEVE_USER_ALL_FOR_MARKETING("R001", "RUAFM", "모든 User에 대해서 마케팅에 필요한 정보를 조회할 수 있는 권한", ""),
    AUTH_RETRIEVE_USER_ALL_FOR_DELIVERY("R002", "RUAFD", "모든 User에 대해서 배송에 필요한 정보를 조회할 수 있는 권한", "");

    private final String code;
    private final String alias;
    private final String desc;
    private final String status;

    public static AuthorityIfEnum getAuthorityFromCode(String code) {
        return Arrays.stream(values())
                .filter(authorityEnum -> authorityEnum.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make AuthorityEnum from value. Unknown code: " + code));
    }

    public static AuthorityIfEnum getAuthorityFromAlias(String alias) {
        return Arrays.stream(values())
                .filter(authorityEnum -> authorityEnum.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make AuthorityEnum from value. Unknown alias: " + alias));
    }

    public static AuthorityIfEnum getAuthorityFromDesc(String desc) {
        return Arrays.stream(values())
                .filter(authorityEnum -> authorityEnum.getDesc().equals(desc))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot make AuthorityEnum from name. Unknown desc: " + desc));
    }
}
