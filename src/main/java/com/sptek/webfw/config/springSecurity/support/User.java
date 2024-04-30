package com.sptek.webfw.config.springSecurity.support;

import com.sptek.webfw.config.springSecurity.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
public class User {
    private long id;
    private String email;
    private String password;
    private UserRole userRole;

    @Builder
    public User(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
}
