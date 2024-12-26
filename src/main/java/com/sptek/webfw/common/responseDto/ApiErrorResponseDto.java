package com.sptek.webfw.common.responseDto;

import com.sptek.webfw.common.code.BaseCode;
import com.sptek.webfw.util.ReqResUtil;
import com.sptek.webfw.util.SpringUtil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
//rest Api에서 실패 응답 규격
HttpStatus.BAD_REQUEST(400)
{
  "resultCode": "GE011",
  "resultMessage": "NOT_VALID_ERROR",
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
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiErrorResponseDto {
    private String resultCode;
    private String resultMessage;
    private String requestTimestamp;
    private String responseTimestamp;
    private String durationMsec;
    private List<InValidFieldInfo> inValidFieldInfos;
    private String exceptionMessage;

    ApiErrorResponseDto(final BaseCode errorCodeEnum) {
        this.resultCode = errorCodeEnum.getResultCode();
        this.resultMessage = errorCodeEnum.getResultMessage();
        this.makeTimestamp();
    }

    ApiErrorResponseDto(final BaseCode errorCodeEnum, final String exceptionMessage) {
        this.resultCode = errorCodeEnum.getResultCode();
        this.resultMessage = errorCodeEnum.getResultMessage();
        this.exceptionMessage = exceptionMessage;
        this.makeTimestamp();
    }

    ApiErrorResponseDto(final BaseCode errorCodeEnum, final String exceptionMessage, final List<InValidFieldInfo> inValidFieldInfos) {
        this.resultCode = errorCodeEnum.getResultCode();
        this.resultMessage = errorCodeEnum.getResultMessage();
        this.exceptionMessage = exceptionMessage;
        this.inValidFieldInfos = inValidFieldInfos;
        this.makeTimestamp();
    }

    public static ApiErrorResponseDto of(final BaseCode errorCodeEnum) {
        return new ApiErrorResponseDto(errorCodeEnum);
    }

    public static ApiErrorResponseDto of(final BaseCode errorCodeEnum, final String exceptionMessage) {
        return new ApiErrorResponseDto(errorCodeEnum, exceptionMessage);
    }

    public static ApiErrorResponseDto of(final BaseCode errorCodeEnum, final String exceptionMessage, final BindingResult bindingResult) {
        return new ApiErrorResponseDto(errorCodeEnum, exceptionMessage, InValidFieldInfo.of(bindingResult));
    }

    public void makeTimestamp() {
        this.requestTimestamp = Optional.ofNullable(ReqResUtil.getRequest().getAttribute(SpringUtil.getProperty("request.reserved.attribute.requestTimeStamp", "REQUEST_TIME_STAMP"))).map(Object::toString).orElse("");
        this.responseTimestamp = LocalDateTime.now().toString();
        this.durationMsec = StringUtils.hasText(requestTimestamp) ? Duration.between(LocalDateTime.parse(requestTimestamp), LocalDateTime.parse(responseTimestamp)).toMillis() + " ms" : "";
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