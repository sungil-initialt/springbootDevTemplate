package com.sptek._frameworkWebCore._example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleABDto {
    private String abString1;
    private String abString2;
    private ExampleADto exampleADto;
    private ExampleBDto exampleBDto;

}
