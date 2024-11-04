package com.sptek.webfw.config.springSecurity.extras.dto;

import com.sptek.webfw.config.springSecurity.extras.entity.UserAddress;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class SignupRequestDto {

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
    private List<RoleDto> roles;

    @NotNull(message = "MEMBER_SHIP 약관은 필수 동의 사항 입니다.")
    //약관 동의
    private List<TermsDto> terms;

    private List<RoleDto> allRoles;

    private List<TermsDto> allTerms;

    private List<String> userRoleNames;
    public List<String> getUserRoleNames() {
        return Optional.ofNullable(roles).orElseGet(Collections::emptyList).stream().map(role -> role.getRoleName()).collect(Collectors.toList());
    }

    private List<String> userTermsNames;
    public List<String> getUserTermsNames() {
        return Optional.ofNullable(terms).orElseGet(Collections::emptyList).stream().map(terms -> terms.getTermsName()).collect(Collectors.toList());
    }
}