package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.RoleEnum;
import lombok.Builder;
import lombok.Data;

@Data
public class User {
    private long id;
    private String email;
    private String password;
    private RoleEnum roleEnum;

    @Builder
    public User(String email, String password, RoleEnum roleEnum) {
        this.email = email;
        this.password = password;
        this.roleEnum = roleEnum;
    }
}
