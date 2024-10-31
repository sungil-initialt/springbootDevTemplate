package com.sptek.webfw.config.springSecurity.extras.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestDto {
    private long id;
    private String key;
    private String value;
}
