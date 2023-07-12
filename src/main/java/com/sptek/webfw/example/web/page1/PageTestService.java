package com.sptek.webfw.example.web.page1;

import com.sptek.webfw.example.web.page1.dto.TbTest;
import com.sptek.webfw.persistence.dao.MyBatisCommonDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PageTestService {

    @Autowired
    private MyBatisCommonDao myBatisCommonDao;

    public int return1(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null, Integer.class);
    }

    @Transactional(readOnly = false)
    public int replicationMasterTest(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null, Integer.class);
    }

    @Transactional(readOnly = true)
    public int replicationSlaveTest(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null, Integer.class);
    }

    @Transactional(readOnly = true)
    public TbTest selectOneTest(){
        int limit = 1;
        TbTest tbTest = this.myBatisCommonDao.selectOne("PageTestMapper.selecWithLimit", limit, TbTest.class);

        return tbTest;
    }

    @Transactional(readOnly = true)
    public List<TbTest> selectListTest(){
        int limit = 100;
        List<TbTest> tbTests = this.myBatisCommonDao.selectList("PageTestMapper.selecWithLimit", limit, TbTest.class);

        return tbTests;
    }

    @Transactional(readOnly = true)
    public Map<?, ?> selectMapTest(){
        int limit = 1;
        Map<?, ?> resultMap = this.myBatisCommonDao.selectMap("PageTestMapper.selecWithLimit", limit, "c1");

        return resultMap;
    }

    @Transactional(readOnly = false)
    public int insertTest(TbTest tbTest){
        int result = this.myBatisCommonDao.insert("PageTestMapper.insertTbtest", tbTest);
        return result;
    }

    @Transactional(readOnly = false)
    public int updateTest(TbTest tbTest){
        int result = this.myBatisCommonDao.insert("PageTestMapper.updateTbtest", tbTest);
        return result;
    }

    @Transactional(readOnly = false)
    public int deleteTest(TbTest tbTest){
        int result = this.myBatisCommonDao.insert("PageTestMapper.deleteTbtest", tbTest);
        return result;
    }
}
