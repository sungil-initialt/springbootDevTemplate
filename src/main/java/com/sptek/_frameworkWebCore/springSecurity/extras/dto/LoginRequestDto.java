package com.sptek._frameworkWebCore.springSecurity.extras.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotEmpty(message = "username을 입력해 주세요.")
    @Email(message = "username이 이메일 형식에 맞지 않습니다.")
    private String username;

    //최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야함
    @Pattern(message = "비밀번호는 최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야 합니다.", regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-])[가-힣0-9a-zA-Z!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-]{6,20}$")
    private String password;
}
