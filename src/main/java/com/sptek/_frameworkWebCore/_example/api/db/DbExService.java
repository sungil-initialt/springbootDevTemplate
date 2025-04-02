package com.sptek._frameworkWebCore._example.api.db;

import com.sptek._frameworkWebCore.persistence.mybatis.dao.MyBatisCommonDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service

public class DbExService {
    private final MyBatisCommonDao myBatisCommonDao;

    @Transactional(readOnly = true)
    public int checkDbConnection(){
        return this.myBatisCommonDao.selectOne("framework_example.return1", null);
    }

    @Transactional(readOnly = false) //master 쪽으로 요청됨.
    public int checkReplicationMaster(){
        return this.myBatisCommonDao.selectOne("framework_example.return1", null);
    }

    @Transactional(readOnly = true) //slave 쪽으로 요청됨.
    public int checkReplicationSlave() {
        return this.myBatisCommonDao.selectOne("framework_example.return1", null);
    }
}
