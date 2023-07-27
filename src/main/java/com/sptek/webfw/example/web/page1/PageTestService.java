package com.sptek.webfw.example.web.page1;

import com.sptek.webfw.example.dto.TBTestDto;
import com.sptek.webfw.example.dto.TBZipcodeDto;
import com.sptek.webfw.persistence.dao.MyBatisCommonDao;
import com.sptek.webfw.support.CommServiceSupport;
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
public class PageTestService extends CommServiceSupport {

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
    public TBTestDto selectOneTest(){
        int limit = 1;
        TBTestDto tbTestDto = this.myBatisCommonDao.selectOne("PageTestMapper.selecWithLimit", limit);

        return tbTestDto;
    }

    @Transactional(readOnly = true)
    public List<TBTestDto> selectListTest(){
        int limit = 100;
        List<TBTestDto> tbTestDtos = this.myBatisCommonDao.selectList("PageTestMapper.selecWithLimit", limit);

        return tbTestDtos;
    }

    @Transactional(readOnly = true)
    public List<TBZipcodeDto> selectListWithResultHandlerTest(){
        MybatisResultHandlerSupport mybatisResultHandlerSupport = new MybatisResultHandlerSupport<TBZipcodeDto, TBZipcodeDto>(){
            int maxProcessCount = 0;
            @Override
            public TBZipcodeDto handleResultRow(TBZipcodeDto resultRow) {
                //전체 처리건수를 10건으로 제한하면서 zipNO 값이 12보다 작은 데이터를  제외하는 간단한 예시
                maxProcessCount++;
                if(Integer.parseInt(resultRow.getZipNo()) < 14040) return null;
                if(maxProcessCount == 10) stop();
                return resultRow;
            }
        };

        List<TBZipcodeDto> tBZipcode = this.myBatisCommonDao.selectListWithResultHandler("PageTestMapper.selectAll",
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
    public PageInfoSupport<TBZipcodeDto> selectPaginateTest(int currentPageNum, int setRowSizePerPage, int setButtomPageNavigationSize) {
        return this.myBatisCommonDao.selectPaginatedList("PageTestMapper.selectAll", null,
                currentPageNum, setRowSizePerPage, setButtomPageNavigationSize);
    }

    @Transactional(readOnly = false)
    public int insertTest(TBTestDto tbTestDto){
        int result = this.myBatisCommonDao.insert("PageTestMapper.insertTbtest", tbTestDto);
        return result;
    }

    @Transactional(readOnly = false)
    public int updateTest(TBTestDto tbTestDto){
        int result = this.myBatisCommonDao.insert("PageTestMapper.updateTbtest", tbTestDto);
        return result;
    }

    @Transactional(readOnly = false)
    public int deleteTest(TBTestDto tbTestDto){
        int result = this.myBatisCommonDao.insert("PageTestMapper.deleteTbtest", tbTestDto);
        return result;
    }
}
