package com.sptek._frameworkWebCore.base.exception;

import com.sptek._frameworkWebCore.base.code.BaseCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
ServiceException 은 RuntimeException을 상속받으며 ErrorCode와 exceptionMessage로 구성됨. (exceptionMessage 은 없을수 있음)
서비스 로직상의 에러?를 처리하기 위해 사용함 (ex:고객님은 휴면 고객 입니다...와 같이 코드상의 에러가 아닌 케이스에 적용)
throw new ServiceException(ErrorCode.SERVICE_XXXX_ERROR, "최근 구매내역 없음"); 와 같이 개발자가 직접 메시지를 넣어준다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// todo : RuntimeException 과 Exception(Checked) 중 어느 것을 상속 해야 좋을지 고민이 있음..
public class ServiceException extends RuntimeException {
    private BaseCode serviceErrorCodeEnum;

    public ServiceException(BaseCode serviceErrorCodeEnum) {
        super(serviceErrorCodeEnum.getResultMessage());
        this.serviceErrorCodeEnum = serviceErrorCodeEnum;
    }

    public ServiceException(BaseCode serviceErrorCodeEnum, String exceptionMessage) {
        super(exceptionMessage);
        this.serviceErrorCodeEnum = serviceErrorCodeEnum;
    }
}