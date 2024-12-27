package com.sptek.webfw.common.responseDto;

import com.sptek.webfw.common.code.BaseCode;
import com.sptek.webfw.common.code.SuccessCodeEnum;
import com.sptek.webfw.util.SpringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/*
//rest Api에서 성공 응답 규격
HttpStatus.OK(200)
{
  "resultCode": "S000",
  "resultMessage": "success",
  "requestTime" : "2024-12-27T14:29:31.827941",
  "responseTime" : "2024-12-27T14:29:31.848168",
  "durationMsec" : "20",
  "result": { -->> T 객체에 선언된 내용으로 구성됨.
    "name": "myProject",
    "version": "v1",
    "description": "sptek web framework"
  }
}
 */
@Slf4j
@Getter
public class ApiSuccessResponseDto<T> {
    private final String resultCode;
    private final String resultMessage;
    private String requestTime;
    private String responseTime;
    private String durationMsec;
    private final T result;

    public ApiSuccessResponseDto(final T result) {
        this.resultCode = SuccessCodeEnum.DEFAULT_SUCCESS.getResultCode();
        this.resultMessage = SuccessCodeEnum.DEFAULT_SUCCESS.getResultMessage();
        this.result = result;
        this.makeTimestamp();
    }

    //성공 응답은 기본적으로 SuccessCodeEnum 안에서 선택함, SuccessCodeEnum에 따라 메시지지는 자동 결정, T는 "result" 필드에 들어가 object
    public ApiSuccessResponseDto(final BaseCode successCodeEnum, final T result) {
        this.resultCode = successCodeEnum.getResultCode();
        this.resultMessage = successCodeEnum.getResultMessage();
        this.result = result;
        this.makeTimestamp();
    }

    //SuccessCodeEnum 안에서 선택할 수 없는 특별한 경우에만 사용.
    public ApiSuccessResponseDto(final String resultCode, final String resultMessage, final T result) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.result = result;
        this.makeTimestamp();
    }

    public void makeTimestamp() {
        this.requestTime = Optional.ofNullable(SpringUtil.getRequest().getAttribute(SpringUtil.getProperty("request.reserved.attribute.requestTimeStamp", "REQUEST_TIME_STAMP"))).map(Object::toString).orElse("");
        this.responseTime = LocalDateTime.now().toString();
        this.durationMsec = StringUtils.hasText(requestTime) ? String.valueOf(Duration.between(LocalDateTime.parse(requestTime), LocalDateTime.parse(responseTime)).toMillis()) : "";
    }
}