package com.sptek._frameworkWebCore.example.view.domain1;

import com.sptek._frameworkWebCore.example.dto.TBTestDto;
import com.sptek._frameworkWebCore.example.dto.TBZipcodeDto;
import com.sptek._frameworkWebCore.persistence.dao.MyBatisCommonDao;
import com.sptek._frameworkWebCore.support.CommonServiceSupport;
import com.sptek._frameworkWebCore.support.MybatisResultHandlerSupport;
import com.sptek._frameworkWebCore.support.PageInfoSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class Domain1ViewService extends CommonServiceSupport {

    private final MyBatisCommonDao myBatisCommonDao;

    @Transactional(readOnly = true)
    public int returnOne(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = false) //false 임으로 master 쪽으로 요청됨.
    public int replicationMaster(){
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = true) //true 임으로 slave 쪽으로 요청됨.
    public int replicationSlave() {
        return this.myBatisCommonDao.selectOne("PageTestMapper.return1", null);
    }

    @Transactional(readOnly = true)
    public TBTestDto selectOne() {
        int SqlParamForlimit = 1;
        return this.myBatisCommonDao.selectOne("PageTestMapper.selecWithLimit", SqlParamForlimit);
    }

    @Transactional(readOnly = true)
    public List<TBTestDto> selectList() {
        int SqlParamForlimit = 100;
        return this.myBatisCommonDao.selectList("PageTestMapper.selecWithLimit", SqlParamForlimit);
    }

    @Transactional(readOnly = true)
    //DB로 부터 result row를 하나씩 받아와 처리하는 용도 (대용량 결과라 한번에 받기 어려운 경우 또는 result row의 결과를 보고 처리가 필요한 경우 사용)
    public List<TBZipcodeDto> selectListWithResultHandler(){
        MybatisResultHandlerSupport mybatisResultHandlerSupport = new MybatisResultHandlerSupport<TBZipcodeDto, TBZipcodeDto>() {
            int maxCount = 0;

            @Override
            //result row 단위로 해야할 작업을 정의한다.
            public TBZipcodeDto handleResultRow(TBZipcodeDto resultRow) {
                //ex) 전체 처리건수를 10건으로 제한하면서 zipNO 값이 특정 값보다 작은 데이터를 제외하는 간단한 예시
                maxCount++;
                if(Integer.parseInt(resultRow.getZipNo()) < 14040) return null;
                if(maxCount == 10) stop();
                return resultRow;
            }

            //필요시 override
            /*
            @Override
            public void open(){
                log.info("called open");
            }

             */
            //필요시 override
            /*
            @Override
            public void close(){
                log.info("called close");
            }
             */
        };

        return this.myBatisCommonDao.selectListWithResultHandler("PageTestMapper.selectAll", null, mybatisResultHandlerSupport);
    }

    @Transactional(readOnly = true)
    public Map<?, ?> selectMap() {
        int SqlParamForlimit = 3;
        //"컬럼명 c1의 값을 map의 key값으로 하여 Map을 생성한다.
        Map<?, ?> resultMap = this.myBatisCommonDao.selectMap("PageTestMapper.selecWithLimit", SqlParamForlimit, "c1");

        return resultMap;
    }

    @Transactional(readOnly = true)
    //result row의 페이징 처리를 위한 예시
    //파람의 상세 내용은 PageInfoSupport 클레스에서 확인가능
    public PageInfoSupport<TBZipcodeDto> selectPaginate(int currentPageNum, int setRowSizePerPage, int setButtomPageNavigationSize) {
        return this.myBatisCommonDao.selectPaginatedList("PageTestMapper.selectAll", null,
                currentPageNum, setRowSizePerPage, setButtomPageNavigationSize);
    }

    @Transactional(readOnly = false)
    public int insert(TBTestDto tbTestDto) {
        return this.myBatisCommonDao.insert("PageTestMapper.insertTbtest", tbTestDto);
    }

    @Transactional(readOnly = false)
    public int update(TBTestDto tbTestDto) {
        return this.myBatisCommonDao.insert("PageTestMapper.updateTbtest", tbTestDto);
    }

    @Transactional(readOnly = false)
    public int delete(TBTestDto tbTestDto) {
        return this.myBatisCommonDao.insert("PageTestMapper.deleteTbtest", tbTestDto);
    }
}
