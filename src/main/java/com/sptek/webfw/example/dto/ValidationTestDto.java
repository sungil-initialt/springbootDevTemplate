package com.sptek.webfw.example.dto;

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
    @NotBlank(message = "NotBlank error") //message값은 Exception 발생시 Exception의 메시지 값으로 처리됨.
    @Pattern(regexp = "^[a-zA-Z0-9]{1,20}$")
    private String userId;

    @NotBlank(message = "NotBlank error")
    @Size(min=2, max=20, message = "Size error")
    private String userName;

    @NotNull(message = "NotNull error")
    @Min(value = 0, message = "Min error")
    @Max(value = Integer.MAX_VALUE, message = "Max error")
    private int age;

    @Email
    private String email;

    @Pattern(regexp = "010\\d{8}", message = "Pattern error")
    private String mobileNumber;

    /*
    @Positive //양수
    @PositiveOrZero //0포함 양수
    @Negative //음수
    @NegativeOrZero //0포함 음수
    @AssertTrue //true만
    @AssertFalse //false만
    @Pattern(regexp = "^[가-힣0-9a-zA-Z]{1,20}$") //한글,숫자,영문(대소문) 20자
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{1,20}$") //영문,수자,특수문자최소1개인 20자
     */
}
