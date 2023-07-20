package com.sptek.webfw.example.web.page1;

import com.sptek.webfw.example.web.page1.dto.TBTest;
import com.sptek.webfw.example.web.page1.dto.TBZipcode;
import com.sptek.webfw.persistence.dao.MyBatisCommonDao;
import com.sptek.webfw.support.MybatisResultHandlerSupport;
import com.sptek.webfw.support.PageInfoSupport;
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

    @Transactional(readOnly = true)
    public int return1(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = false)
    public int replicationMasterTest(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = true)
    public int replicationSlaveTest(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = true)
    public TBTest selectOneTest(){
        int limit = 1;
        TBTest tbTest = this.myBatisCommonDao.selectOne("PageTestMapper.selecWithLimit", limit);

        return tbTest;
    }

    @Transactional(readOnly = true)
    public List<TBTest> selectListTest(){
        int limit = 100;
        List<TBTest> tbTests = this.myBatisCommonDao.selectList("PageTestMapper.selecWithLimit", limit);

        return tbTests;
    }

    @Transactional(readOnly = true)
    public List<TBZipcode> selectListWithResultHandlerTest(){
        MybatisResultHandlerSupport mybatisResultHandlerSupport = new MybatisResultHandlerSupport<TBZipcode, TBZipcode>(){
            int maxProcessCount = 0;
            @Override
            public TBZipcode handleResultRow(TBZipcode resultRow) {
                //전체 처리건수를 10건으로 제한하면서 zipNO 값이 12보다 작은 데이터를  제외하는 간단한 예시
                maxProcessCount++;
                if(Integer.parseInt(resultRow.getZipNo()) < 14040) return null;
                if(maxProcessCount == 10) stop();
                return resultRow;
            }
        };

        List<TBZipcode> tBZipcode = this.myBatisCommonDao.selectListWithResultHandler("PageTestMapper.selectAll",
                null, mybatisResultHandlerSupport);
        return tBZipcode;
    }

    @Transactional(readOnly = true)
    public Map<?, ?> selectMapTest(){
        int limit = 3;
        Map<?, ?> resultMap = this.myBatisCommonDao.selectMap("PageTestMapper.selecWithLimit", limit, "c1");

        return resultMap;
    }

    @Transactional(readOnly = true)
    public PageInfoSupport<TBZipcode> selectPaginateTest(int currentPageNum, int setRowSizePerPage, int setButtomPageNavigationSize) {
        return this.myBatisCommonDao.selectPaginatedList("PageTestMapper.selectAll", null,
                currentPageNum, setRowSizePerPage, setButtomPageNavigationSize);
    }

    @Transactional(readOnly = false)
    public int insertTest(TBTest tbTest){
        int result = this.myBatisCommonDao.insert("PageTestMapper.insertTbtest", tbTest);
        return result;
    }

    @Transactional(readOnly = false)
    public int updateTest(TBTest tbTest){
        int result = this.myBatisCommonDao.insert("PageTestMapper.updateTbtest", tbTest);
        return result;
    }

    @Transactional(readOnly = false)
    public int deleteTest(TBTest tbTest){
        int result = this.myBatisCommonDao.insert("PageTestMapper.deleteTbtest", tbTest);
        return result;
    }
}
