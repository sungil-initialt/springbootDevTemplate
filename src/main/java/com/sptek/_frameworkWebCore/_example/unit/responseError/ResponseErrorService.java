package com.sptek._frameworkWebCore._example.unit.responseError;

import com.sptek._frameworkWebCore.base.exception.ServiceException;
import com.sptek._frameworkWebCore.persistence.mybatis.dao.MyBatisCommonDao;
import com.sptek._projectCommon.code.ServiceErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service

public class ResponseErrorService {

    private final MyBatisCommonDao myBatisCommonDao;

    public boolean isAvailableId_badExample(String userId) throws ServiceException {
        int userIdCount = 1; // int userIdCount = myBatisCommonDao.selectOne("userIdCount", userId); --> 이런 처리가 있다고 가정
        return userIdCount <= 0;
    }

    public boolean isAvailableId_goodExample(String userId) throws ServiceException {
        int userIdCount = 1; //int userIdCount = myBatisCommonDao.selectOne("userIdCount", userId); --> 이런 처리가 있다고 가정

        // 서비스 관련 예외 처리를 리턴 값을 통해 controller 에서 처리 하지 말고 가능 하면 예외가 발생한 위치 에서 바로 ServiceException 을 Raise 한다.
        if (userIdCount > 0) throw new ServiceException(ServiceErrorCodeEnum.ALREADY_EXIST_RESOURCE_ERROR, userId + " 는 사용할 수 없는 아이디 입니다..");
        return userIdCount <= 0;
    }
}
