package com.sptek._frameworkWebCore._example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
어노테이션을 사용하여 input 값들에 대한 validation을 처리하는 예시 (주로 많이 사용하는 것들 위주)
이러한 어노테이션을 방식을 많이 활용하도록 권장 (EX 처리등에도 많은 이점이 있다)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationTestDto {


    //NotBlank은 NotNull, NotEmpty 기능을 모두 포함함.
    @NotBlank(message = "userId을 입력해 주세요") //message값은 Exception 발생시 Exception의 메시지 값으로 처리됨.
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "userId는 영문자와 숫자로만 입력해 주세요.")
    @Schema(description = "사용자 ID", example = "sungilry")
    private String userId;

    @NotBlank(message = "userName을 입력해 주세요")
    @Size(min=2, max=20, message = "userName은 2자 이상 20자 이하로 해주세요")
    @Schema(description = "사용자 이름", example = "이성일")
    private String userName;

    @NotNull(message = "age을 입력해 주세요")
    @Min(value = 0, message = "age은 0보다 커야 합니다.")
    @Max(value = Integer.MAX_VALUE, message = "age가 너무 큽니다.")
    @Schema(description = "사용자 나이", example = "20")
    private int age;

    @Email(message = "email이 이메일 형식에 맞지 않습니다.")
    @Schema(description = "사용자 이메일", example = "sungilry@sptek.co.kr")
    private String email;

    @Pattern(regexp = "010\\d{8}", message = "전화번호 형식에 맞지 않습니다.")
    @Schema(description = "사용자 전화번호", example = "010-1234-5678")
    private String mobileNumber;

    /*
    @Valid //객체 내부 까지 벨리드 검사
    @URL //url 형식
    @Positive //양수
    @PositiveOrZero //0포함 양수
    @Negative //음수
    @NegativeOrZero //0포함 음수
    @AssertTrue //true만
    @AssertFalse //false만
    @Pattern(regexp = "^[가-힣0-9a-zA-Z]{1,20}$") //한글,숫자,영문(대소문) 20자
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{1,20}$") //영문,수자,특수문자최소1개인 20자

    //비밀번호 예시 (최소6자, 최대20자, 숫자, 대문자, 특수문자가 각각 최소 1개 이상 들어가야함
    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-])[가-힣0-9a-zA-Z!@#$%^&*()_+{}\\[\\]:;<>,.?/~`\"-]{6,20}$")
     */
}
