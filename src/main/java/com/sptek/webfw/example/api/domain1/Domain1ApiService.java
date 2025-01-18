package com.sptek.webfw.example.api.domain1;

import com.sptek.webfw.base.code.ServiceErrorCodeEnum;
import com.sptek.webfw.base.exception.ServiceException;
import com.sptek.webfw.support.CommonServiceSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Domain1ApiService extends CommonServiceSupport {
    public int raiseServiceError(int errorCaseNum) throws ServiceException {

        switch (errorCaseNum) {
            case 1:
                throw new ServiceException(ServiceErrorCodeEnum.ALREADY_EXIST_RESOURCE_ERROR);

            case 2:
                throw new ServiceException(ServiceErrorCodeEnum.NO_RESOURCE_ERROR, "해당 기간동안의 주문 내역이 없습니다.");
        }

        throw new ServiceException(ServiceErrorCodeEnum.XXX_ERROR);
        //return errorCaseNum;
    }

}
