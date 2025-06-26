package com.sptek._frameworkWebCore._example.dto;

import com.sptek._projectCommon.commonDtos.PostBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExamplePostDto extends PostBaseDto {
    String title;
    String content;
}
