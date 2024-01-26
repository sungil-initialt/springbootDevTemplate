package com.sptek.webfw.exception;

import com.sptek.webfw.code.ApiErrorCode;
import lombok.Builder;
import lombok.Getter;

/*
ServiceException 은 RuntimeException을 상속받으며 apiErrorCode와 exceptionMessage로 구성됨. (exceptionMessage 은 없을수 있음)
서비스 로직상의 에러?를 처리하기 위해 사용함 (ex:최근 구매한 상품이 없는 경우 같은..)
throw new ApiServiceException(ApiErrorCode.SERVICE_DEFAULT_ERROR, "최근 구매내역 없음"); 와 같이 개발자가 직접 메시지를 넣어준다.
 */
public class ApiServiceException extends RuntimeException {

    @Getter
    private final ApiErrorCode apiErrorCode;

    @Builder
    public ApiServiceException(ApiErrorCode apiErrorCode, String exceptionMessage) {
        super(exceptionMessage);
        this.apiErrorCode = apiErrorCode;
    }

    @Builder
    public ApiServiceException(ApiErrorCode apiErrorCode) {
        super();
        this.apiErrorCode = apiErrorCode;
    }
}