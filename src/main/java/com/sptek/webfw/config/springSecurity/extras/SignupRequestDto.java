package com.sptek.webfw.config.springSecurity.extras;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {
    @NotBlank(message = "name을 입력해 주세요")
    @Size(min = 2, max = 20, message = "name은 2자 이상 20자 이하로 입력해 주세요.")
    @Schema(description = "사용자 이름", example = "이성일")
    private String name;

    @NotNull
    @Email(message = "email이 이메일 형식에 맞지 않습니다.")
    private String email;

    @NotNull
    //최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야함
    @Pattern(message = "비밀번호는 최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야 합니다.", regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-])[가-힣0-9a-zA-Z!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-]{6,20}$")
    private String password;

    @NotNull(message = "하나 이상의 Role을 선택해 주세요.")
    private List<String> roleNames;

    @NotNull(message = "MEMBER_SHIP 약관은 필수 동의 사항 입니다.")
    //약관 동의
    private List<String> termsNames;


}