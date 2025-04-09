package com.sptek._frameworkWebCore._example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ExUserDto {
    @Schema(description = "사용자 ID", example = "sungilry")
    private String id;

    @Schema(description = "사용자 이름", example = "이성일")
    private String name;

    @Schema(description = "사용자 타입", example = "customer")
    private UserType type;

    public enum UserType {
        customer, manager, admin, anonymous
    }
}