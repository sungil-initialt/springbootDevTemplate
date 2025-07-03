package com.sptek._frameworkWebCore._example.dto;

import com.sptek._projectCommon.commonObject.dto.PostBaseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ExamplePostDto extends PostBaseDto {
    String title;
    String content;
}
