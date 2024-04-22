package com.sptek.webfw.config.springSecurity.service;

import com.sptek.webfw.config.springSecurity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    private String email;
    private String password;
    private UserRole userRole;

    @Builder
    private User(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }
}
