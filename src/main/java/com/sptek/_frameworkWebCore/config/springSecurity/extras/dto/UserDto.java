package com.sptek._frameworkWebCore.config.springSecurity.extras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
