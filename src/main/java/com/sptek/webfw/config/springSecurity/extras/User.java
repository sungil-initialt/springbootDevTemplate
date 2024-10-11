package com.sptek.webfw.config.springSecurity.extras;

import com.sptek.webfw.config.springSecurity.UserRoleEnum;
import lombok.Builder;
import lombok.Data;

@Data
public class User {
    private long id;
    private String email;
    private String password;
    private UserRoleEnum userRole;

    @Builder
    public User(String email, String password, UserRoleEnum userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
}
