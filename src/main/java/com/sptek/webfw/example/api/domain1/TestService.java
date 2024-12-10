package com.sptek.webfw.example.api.domain1;

import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.support.CommonServiceSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TestService extends CommonServiceSupport {
    public int raiseServiceError(int errorType) throws ServiceException {

        switch (errorType) {
            case 1:
                throw new ServiceException(ServiceErrorCodeEnum.ALREADY_EXIST_RESOURCE_ERROR);

            case 2:
                throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "해당 기간동안의 주문 내역이 없습니다.");
        }

        return errorType;
    }

}
