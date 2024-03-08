package com.sptek.webfw.commonDto;

import com.sptek.webfw.code.SuccessCode;
import lombok.Getter;

/*
//rest Api에서 성공 응답 규격
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
public class ApiSuccessResponse<T> {

    private String resultCode;
    private String resultMessage;
    private T result;

    //성공 응답은 기본적으로 SuccessCode enum 안에서 선택함, SuccessCode에 따라 메시지지는 자동 결정, T는 "result" 필드에 들어가 object
    public ApiSuccessResponse(final SuccessCode successCode, final T result) {
        this.resultCode = successCode.getResultCode();
        this.resultMessage = successCode.getResultMessage();
        this.result = result;
    }

    //SuccessCode enum 안에서 선택할 수 없는 특별한 경우에 사용.
    public ApiSuccessResponse(final String resultCode, final String resultMessage, final T result) {
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.result = result;
    }
}