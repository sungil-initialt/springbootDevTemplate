package com.sptek._frameworkWebCore.config.springSecurity.extras.dto;

import com.sptek._frameworkWebCore.config.springSecurity.AuthorityIfEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto {
    private Long id;

    @NotNull
    private AuthorityIfEnum authority;
}
