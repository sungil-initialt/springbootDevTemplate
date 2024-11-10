package com.sptek.webfw.config.springSecurity.extras.dto;

import com.sptek.webfw.config.springSecurity.extras.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleMngRequestDto {
    @Valid //내부 객체에 대해서도 Valid 기능을 동작하게 함
    private List<RoleDto> allRoles;

    @Valid
    private List<AuthoritytDto> allAuthorities;
}