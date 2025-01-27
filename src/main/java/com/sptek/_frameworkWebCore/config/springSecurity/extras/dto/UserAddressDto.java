package com.sptek._frameworkWebCore.config.springSecurity.extras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAddressDto {
    private Long id;
    private String addressType;
    private String address;
}
