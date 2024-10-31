package com.sptek.webfw.config.springSecurity.extras.dto;

import com.sptek.webfw.config.springSecurity.RoleEnum;
import com.sptek.webfw.config.springSecurity.extras.entity.Role;
import com.sptek.webfw.config.springSecurity.extras.entity.Terms;
import com.sptek.webfw.config.springSecurity.extras.entity.UserAddress;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private long id;
    private String name;
    private String email;
    private String password;
    private List<UserAddressDto> userAddresses;
    private Set<RoleDto> roles;
    private Set<TermsDto> terms;

}
