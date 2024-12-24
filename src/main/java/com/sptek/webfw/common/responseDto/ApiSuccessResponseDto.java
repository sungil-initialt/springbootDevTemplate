package com.sptek.webfw.common.responseDto;

import com.sptek.webfw.common.code.BaseCode;
import com.sptek.webfw.common.code.SuccessCodeEnum;
import com.sptek.webfw.util.ReqResUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.Instant;

/*
//rest Api에서 성공 응답 규격
HttpStatus.OK(200)
{
  "resultCode": "S000",
  "resultMessage": "success",
  "result": { -->> T 객체에 선언된 내용으로 구성됨.
    "name": "myProject",
    "version": "v1",
    "description": "sptek web framework"
  }
}
 */
@Getter
public class ApiSuccessResponseDto<T> {
    private String resultCode;
    private String resultMessage;
    private T result;
    private String requestTimestamp;
    private String responseTimestamp;
    private String durationMsec;

    public ApiSuccessResponseDto(final T result) {
        this.resultCode = SuccessCodeEnum.DEFAULT_SUCCESS.getResultCode();
        this.resultMessage = SuccessCodeEnum.DEFAULT_SUCCESS.getResultMessage();
        this.result = result;
    }

    //성공 응답은 기본적으로 SuccessCodeEnum 안에서 선택함, SuccessCodeEnum에 따라 메시지지는 자동 결정, T는 "result" 필드에 들어가 object
    public ApiSuccessResponseDto(final BaseCode successCodeEnum, final T result) {
        this.resultCode = successCodeEnum.getResultCode();
        this.resultMessage = successCodeEnum.getResultMessage();
        this.result = result;
    }

    //SuccessCodeEnum 안에서 선택할 수 없는 특별한 경우에만 사용.
    public ApiSuccessResponseDto(final String resultCode, final String resultMessage, final T result) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.result = result;
    }

    @PostConstruct
    public void makeTimestamp(@Value("${request.reserved.attribute.requestTimeStamp}") String requestTimeStampAttributeName) {
        this.requestTimestamp = String.valueOf(ReqResUtil.getRequest().getAttribute(requestTimeStampAttributeName));
        this.responseTimestamp = String.valueOf(Instant.now());

        if(StringUtils.hasText(requestTimestamp)) {
            this.durationMsec = Duration.between(Instant.parse(requestTimestamp), Instant.parse(responseTimestamp)).toMillis() + " ms";
        }
    }
    //--> 여기부터 해야함, 타임스템브 왜 안되는지 확인필요, view쪽 응답에 대해서도 타임스템프 대용이 필요한지 확인 필요, "/api" 키타 키값에 대한 상수 처리, 컨트럴 외부 에러 로깅이 안되는 문제에 대한 대응 처리 필요
}