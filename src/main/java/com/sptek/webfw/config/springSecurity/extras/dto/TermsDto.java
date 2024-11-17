package com.sptek.webfw.config.springSecurity.extras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TermsDto {
    private Long id;
    private String termsName;
}
