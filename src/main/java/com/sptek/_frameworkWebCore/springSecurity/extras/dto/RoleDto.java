package com.sptek._frameworkWebCore.springSecurity.extras.dto;

import com.sptek._frameworkWebCore.springSecurity.AuthorityEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleDto {
    private Long id;

    @Size(min = 2, max = 20, message = "role Name 은 2자 이상 20자 이하로 입력해 주세요.")
    @Schema(description = "롤 이름", example = "ROLE_USER")
    private String roleName;

    private List<AuthorityDto> authorities;

    @Setter(AccessLevel.NONE)
    private List<AuthorityEnum> authorityEnums;

    public List<AuthorityEnum> getAuthorityEnums() {
        return Optional.ofNullable(authorities).orElseGet(Collections::emptyList)
                .stream().map(AuthorityDto::getAuthority).collect(Collectors.toList());
    }
}
