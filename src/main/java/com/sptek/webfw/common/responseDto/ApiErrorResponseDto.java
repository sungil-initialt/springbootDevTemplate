package com.sptek.webfw.common.responseDto;

import com.sptek.webfw.common.code.ErrorCodeEnum;
import lombok.*;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
//rest Api에서 실패 응답 규격
{
  "resultCode": "GE011",
  "resultMessage": "handle Validation Exception",
  "inValidFieldInfos": [
    {
      "field": "userName",
      "value": "s",
      "reason": "Size error"
    }
  ],
  "exceptionMessage": "Validation failed for argument [0] in protected org.springframework.http.ResponseEntity&lt;com.sptek.webfw.dto.ApiSuccessResponseDto&lt;com.sptek.webfw.example.dto.ValidationTestDto&gt;&gt; com.sptek.webfw.example.api.api1.ApiTestController.validationAnnotationPost(com.sptek.webfw.example.dto.ValidationTestDto): [Field error in object 'validationTestDto' on field 'userName': rejected value [s]; codes [Size.validationTestDto.userName,Size.userName,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [validationTestDto.userName,userName]; arguments []; default message [userName],20,2]; default message [Size error]] "
}
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiErrorResponseDto {
    private String resultCode;
    private String resultMessage;
    private List<InValidFieldInfo> inValidFieldInfos;
    private String exceptionMessage;

    public ApiErrorResponseDto(final ErrorCodeEnum errorCodeEnum) {
        this.resultCode = errorCodeEnum.getResultCode();
        this.resultMessage = errorCodeEnum.getResultMessage();
    }

    public ApiErrorResponseDto(final ErrorCodeEnum errorCodeEnum, final String exceptionMessage) {
        this.resultCode = errorCodeEnum.getResultCode();
        this.resultMessage = errorCodeEnum.getResultMessage();
        this.exceptionMessage = exceptionMessage;
    }

    public ApiErrorResponseDto(final ErrorCodeEnum errorCodeEnum, final String exceptionMessage, final List<InValidFieldInfo> inValidFieldInfos) {
        this.resultCode = errorCodeEnum.getResultCode();
        this.resultMessage = errorCodeEnum.getResultMessage();
        this.exceptionMessage = exceptionMessage;
        this.inValidFieldInfos = inValidFieldInfos;
    }

    public static ApiErrorResponseDto of(final ErrorCodeEnum errorCodeEnum) {
        return new ApiErrorResponseDto(errorCodeEnum);
    }

    public static ApiErrorResponseDto of(final ErrorCodeEnum errorCodeEnum, final String exceptionMessage) {
        return new ApiErrorResponseDto(errorCodeEnum, exceptionMessage);
    }

    public static ApiErrorResponseDto of(final ErrorCodeEnum errorCodeEnum, final String exceptionMessage, final BindingResult bindingResult) {
        return new ApiErrorResponseDto(errorCodeEnum, exceptionMessage, InValidFieldInfo.of(bindingResult));
    }


    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class InValidFieldInfo {
        private final String field;
        private final String value;
        private final String reason;

        private static List<InValidFieldInfo> of(final String field, final String value, final String reason) {
            List<InValidFieldInfo> inValidFieldInfos = new ArrayList<>();
            inValidFieldInfos.add(new InValidFieldInfo(field, value, reason));

            return inValidFieldInfos;
        }

        private static List<InValidFieldInfo> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();

            return fieldErrors.stream()
                    .map(error -> new InValidFieldInfo(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}