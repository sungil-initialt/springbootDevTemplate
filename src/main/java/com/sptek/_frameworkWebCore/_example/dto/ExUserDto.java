package com.sptek._frameworkWebCore._example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Id를 입력해 주세요")
    private String id;

    @Schema(description = "사용자 이름", example = "이성일")
    @NotBlank(message = "이름을 입력해 주세요")
    private String name;

    @Schema(description = "사용자 타입", example = "customer")
    private UserType type;

    @Schema(hidden = true)
    private String displayName;

    public enum UserType {
        customer, manager, admin, anonymous
    }
}