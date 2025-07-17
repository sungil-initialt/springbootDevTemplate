package com.sptek._frameworkWebCore._example.dto;

import com.sptek._frameworkWebCore.annotation.Enable_DecryptAuto_At_DtoString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleADto {
    @Enable_DecryptAuto_At_DtoString
    private String aDtoFirstName;
    @Enable_DecryptAuto_At_DtoString
    private String aDtoLastName;
}
