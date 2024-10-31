package com.sptek.webfw.config.springSecurity.extras.dto;

import com.sptek.webfw.config.springSecurity.extras.entity.Role;
import com.sptek.webfw.config.springSecurity.extras.entity.Terms;
import com.sptek.webfw.config.springSecurity.extras.entity.UserAddress;
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
public class UserAddressDto {
    private Long id;
    private String addressType;
    private String address;
}
