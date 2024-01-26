package com.sptek.webfw.example.web.page1;

import com.sptek.webfw.example.dto.TBTestDto;
import com.sptek.webfw.example.dto.TBZipcodeDto;
import com.sptek.webfw.persistence.dao.MyBatisCommonDao;
import com.sptek.webfw.support.CommonServiceSupport;
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
public class PageTestService extends CommonServiceSupport {

    @Autowired
    private MyBatisCommonDao myBatisCommonDao;

    @Transactional(readOnly = true)
    public int returnOne(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = false) //false 임으로 master 쪽으로 요청됨.
    public int replicationMasterTest(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = true) //true 임으로 slave 쪽으로 요청됨.
    public int replicationSlaveTest() {
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = true)
    public TBTestDto selectOneTest() {
        int limit = 1;
        return this.myBatisCommonDao.selectOne("PageTestMapper.selecWithLimit", limit);
    }

    @Transactional(readOnly = true)
    public List<TBTestDto> selectListTest() {
        int limit = 100;
        return this.myBatisCommonDao.selectList("PageTestMapper.selecWithLimit", limit);
    }

    @Transactional(readOnly = true)
    public List<TBZipcodeDto> selectListWithResultHandlerTest(){
        MybatisResultHandlerSupport mybatisResultHandlerSupport = new MybatisResultHandlerSupport<TBZipcodeDto, TBZipcodeDto>() {
            int maxProcessCount = 0;
            @Override
            public TBZipcodeDto handleResultRow(TBZipcodeDto resultRow) {
                //전체 처리건수를 10건으로 제한하면서 zipNO 값이 특정 값보다 작은 데이터를  제외하는 간단한 예시
                maxProcessCount++;
                if(Integer.parseInt(resultRow.getZipNo()) < 14040) return null;
                if(maxProcessCount == 10) stop();
                return resultRow;
            }
        };

        return this.myBatisCommonDao.selectListWithResultHandler("PageTestMapper.selectAll", null, mybatisResultHandlerSupport);
    }

    @Transactional(readOnly = true)
    public Map<?, ?> selectMapTest() {
        int limit = 3;
        Map<?, ?> resultMap = this.myBatisCommonDao.selectMap("PageTestMapper.selecWithLimit", limit, "c1");

        return resultMap;
    }

    @Transactional(readOnly = true)
    public PageInfoSupport<TBZipcodeDto> selectPaginateTest(int currentPageNum, int setRowSizePerPage, int setButtomPageNavigationSize) {
        return this.myBatisCommonDao.selectPaginatedList("PageTestMapper.selectAll", null,
                currentPageNum, setRowSizePerPage, setButtomPageNavigationSize);
    }

    @Transactional(readOnly = false)
    public int insertTest(TBTestDto tbTestDto) {
        return this.myBatisCommonDao.insert("PageTestMapper.insertTbtest", tbTestDto);
    }

    @Transactional(readOnly = false)
    public int updateTest(TBTestDto tbTestDto) {
        return this.myBatisCommonDao.insert("PageTestMapper.updateTbtest", tbTestDto);
    }

    @Transactional(readOnly = false)
    public int deleteTest(TBTestDto tbTestDto) {
        return this.myBatisCommonDao.insert("PageTestMapper.deleteTbtest", tbTestDto);
    }
}
