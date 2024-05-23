package com.sptek.webfw.example.api.api1;

import com.sptek.webfw.common.code.ServiceErrorCodeEnum;
import com.sptek.webfw.common.exception.ServiceException;
import com.sptek.webfw.support.CommonServiceSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ApiTestService extends CommonServiceSupport {
    public int raiseServiceError(int errorType) {

        switch (errorType) {
            case 1:
                throw new ServiceException(ServiceErrorCodeEnum.SERVICE_001_ERROR);

            case 2:
                throw new ServiceException(ServiceErrorCodeEnum.SERVICE_002_ERROR);
        }

        return errorType;
    }

    public void justTest(){
        int result = this.raiseServiceError(1);
    }
}
