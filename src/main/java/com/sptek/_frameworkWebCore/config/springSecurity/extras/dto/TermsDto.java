package com.sptek._frameworkWebCore.config.springSecurity.extras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TermsDto {
    private Long id;
    private String termsName;
}
