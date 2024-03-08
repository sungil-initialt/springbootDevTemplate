package com.sptek.webfw.commonDto;

import com.sptek.webfw.code.ErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
  "exceptionMessage": "Validation failed for argument [0] in protected org.springframework.http.ResponseEntity&lt;com.sptek.webfw.dto.ApiSuccessResponse&lt;com.sptek.webfw.example.dto.ValidationTestDto&gt;&gt; com.sptek.webfw.example.api.api1.ApiTestController.validationAnnotationPost(com.sptek.webfw.example.dto.ValidationTestDto): [Field error in object 'validationTestDto' on field 'userName': rejected value [s]; codes [Size.validationTestDto.userName,Size.userName,Size.java.lang.String,Size]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [validationTestDto.userName,userName]; arguments []; default message [userName],20,2]; default message [Size error]] "
}
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiErrorResponse {
    private String resultCode;
    private String resultMessage;
    private List<InValidFieldInfo> inValidFieldInfos;
    private String exceptionMessage;

    @Builder
    protected ApiErrorResponse(final ErrorCode errorCode) {
        this.resultCode = errorCode.getResultCode();
        this.resultMessage = errorCode.getResultMessage();
    }

    @Builder
    protected ApiErrorResponse(final ErrorCode errorCode, final String exceptionMessage) {
        this.resultCode = errorCode.getResultCode();
        this.resultMessage = errorCode.getResultMessage();
        this.exceptionMessage = exceptionMessage;
    }

    @Builder
    protected ApiErrorResponse(final ErrorCode errorCode, final String exceptionMessage, final List<InValidFieldInfo> inValidFieldInfos) {
        this.resultCode = errorCode.getResultCode();
        this.resultMessage = errorCode.getResultMessage();
        this.exceptionMessage = exceptionMessage;
        this.inValidFieldInfos = inValidFieldInfos;
    }

    public static ApiErrorResponse of(final ErrorCode errorCode) {
        return new ApiErrorResponse(errorCode);
    }

    public static ApiErrorResponse of(final ErrorCode errorCode, final String exceptionMessage) {
        return new ApiErrorResponse(errorCode, exceptionMessage);
    }

    public static ApiErrorResponse of(final ErrorCode errorCode, final String exceptionMessage, final BindingResult bindingResult) {
        return new ApiErrorResponse(errorCode, exceptionMessage, InValidFieldInfo.of(bindingResult));
    }



    @Getter
    public static class InValidFieldInfo {
        private final String field;
        private final String value;
        private final String reason;

        @Builder
        InValidFieldInfo(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<InValidFieldInfo> of(final String field, final String value, final String reason) {
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
