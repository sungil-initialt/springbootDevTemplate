package com.sptek.webfw.example.api.api1;

import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.support.CommonServiceSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiTestService extends CommonServiceSupport {
    public int raiseServiceError(int errorType) throws ServiceException {

        switch (errorType) {
            case 1:
                throw new ServiceException(ServiceErrorCodeEnum.SERVICE_001_ERROR);

            case 2:
                throw new ServiceException(ServiceErrorCodeEnum.SERVICE_002_ERROR, "해당 기간동안 주문내역이 없음");
        }

        return errorType;
    }

}
