package com.sptek.webfw.dto;

import com.sptek.webfw.code.ApiErrorCode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiErrorResponse {
    private String resultCode;
    private String resultMessage;
    private List<InValidFieldInfo> inValidFieldInfos;
    private String exceptionMessage;

    @Builder
    protected ApiErrorResponse(final ApiErrorCode apiErrorCode) {
        this.resultCode = apiErrorCode.getResultCode();
        this.resultMessage = apiErrorCode.getResultMessage();
    }

    @Builder
    protected ApiErrorResponse(final ApiErrorCode apiErrorCode, final String exceptionMessage) {
        this.resultCode = apiErrorCode.getResultCode();
        this.resultMessage = apiErrorCode.getResultMessage();
        this.exceptionMessage = exceptionMessage;
    }

    @Builder
    protected ApiErrorResponse(final ApiErrorCode apiErrorCode, final String exceptionMessage, final List<InValidFieldInfo> inValidFieldInfos) {
        this.resultCode = apiErrorCode.getResultCode();
        this.resultMessage = apiErrorCode.getResultMessage();
        this.exceptionMessage = exceptionMessage;
        this.inValidFieldInfos = inValidFieldInfos;
    }

    public static ApiErrorResponse of(final ApiErrorCode apiErrorCode) {
        return new ApiErrorResponse(apiErrorCode);
    }

    public static ApiErrorResponse of(final ApiErrorCode apiErrorCode, final String exceptionMessage) {
        return new ApiErrorResponse(apiErrorCode, exceptionMessage);
    }

    public static ApiErrorResponse of(final ApiErrorCode apiErrorCode, final String exceptionMessage, final BindingResult bindingResult) {
        return new ApiErrorResponse(apiErrorCode, exceptionMessage, InValidFieldInfo.of(bindingResult));
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
