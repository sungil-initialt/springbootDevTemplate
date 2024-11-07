package com.sptek.webfw.config.springSecurity.extras.dto;

import com.sptek.webfw.config.springSecurity.AuthorityEnum;
import lombok.*;
import org.hibernate.sql.ast.tree.expression.Collation;

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
    private String roleName;

    private List<AuthoritytDto> authorities;

    @Setter(AccessLevel.NONE)
    private List<AuthorityEnum> AuthorityEnums;
    public List<AuthorityEnum> getAuthorityNames() {
        return Optional.ofNullable(authorities).orElseGet(Collections::emptyList).stream()
                .map(AuthoritytDto::getAuthority).collect(Collectors.toList());
    }
}
