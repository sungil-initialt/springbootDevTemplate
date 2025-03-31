package com.sptek._frameworkWebCore._example.dto;

import com.sptek._frameworkWebCore.annotation.EnableDecryptAuto_InDtoString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleADto {
    @EnableDecryptAuto_InDtoString
    private String aDtoFirstName;
    @EnableDecryptAuto_InDtoString
    private String aDtoLastName;
}
