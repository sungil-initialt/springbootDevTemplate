package com.sptek.webfw.config.springSecurity.extras.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequestDto {
    private Long id;

    @Size(min = 2, max = 20, message = "name은 2자 이상 20자 이하로 입력해 주세요.")
    @Schema(description = "사용자 이름", example = "이성일")
    private String name;

    @NotEmpty(message = "email을 입력해 주세요.")
    @Email(message = "email이 이메일 형식에 맞지 않습니다.")
    private String email;

    //최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야함
    @Pattern(message = "비밀번호는 최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야 합니다.", regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-])[가-힣0-9a-zA-Z!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-]{6,20}$")
    private String password;

    //주소
    private List<UserAddressDto> userAddresses;

    @NotNull(message = "하나 이상의 Role을 선택해 주세요.")
    private List<RoleAuthorityDto> roleAuthorities;

    @NotNull(message = "MEMBER_SHIP 약관은 필수 동의 사항 입니다.")
    //약관 동의
    private List<TermsDto> terms;

    private List<RoleAuthorityDto> allRoleAuthorities;
    private List<TermsDto> allTerms;

    private List<String> userRoleNames;
    public List<String> getUserRoleNames() {
        return Optional.ofNullable(roleAuthorities).orElseGet(Collections::emptyList).stream().map(RoleAuthorityDto::getRoleName).collect(Collectors.toList());
    }

    private List<String> userTermsNames;
    public List<String> getUserTermsNames() {
        return Optional.ofNullable(terms).orElseGet(Collections::emptyList).stream().map(TermsDto::getTermsName).collect(Collectors.toList());
    }
}