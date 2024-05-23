package com.sptek.webfw.common.responseDto;

import com.sptek.webfw.common.code.BaseCode;
import com.sptek.webfw.common.code.SuccessCodeEnum;
import lombok.Getter;

/*
//rest Api에서 성공 응답 규격
HttpStatus.OK(200)
{
  "resultCode": "S000",
  "resultMessage": "Success",
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
}