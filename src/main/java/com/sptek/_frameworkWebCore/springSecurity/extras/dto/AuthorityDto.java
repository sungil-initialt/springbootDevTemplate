package com.sptek._frameworkWebCore.springSecurity.extras.dto;

import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityDto {
    private Long id;

    @NotNull
    private AuthorityEnum authority;
}
