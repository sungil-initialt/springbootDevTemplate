package com.sptek._frameworkWebCore._example.api.db;

import com.sptek._frameworkWebCore._example.dto.Tb_TestDto;
import com.sptek._frameworkWebCore._example.dto.Tb_ZipcodeDto;
import com.sptek._frameworkWebCore.persistence.mybatis.dao.MyBatisCommonDao;
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

    @Transactional(readOnly = false)
    public int insertTB_Test(Tb_TestDto tbTestDto) {
        return this.myBatisCommonDao.insert("framework_example.insertTb_Test", tbTestDto);
    }

    @Transactional(readOnly = false)
    public int updateTb_Test(Tb_TestDto tbTestDto) {
        return this.myBatisCommonDao.update("framework_example.updateTb_Test", tbTestDto);
    }

    @Transactional(readOnly = false)
    public int deleteTb_Test() {
        return this.myBatisCommonDao.delete("framework_example.deleteTb_Test", null);
    }



    @Transactional(readOnly = true)
    public Tb_TestDto getOneTB_Test() {
        int limit = 1;
        return this.myBatisCommonDao.selectOne("framework_example.selectTb_Test", limit);
    }

    @Transactional(readOnly = true)
    public List<Tb_TestDto> getListTB_Test() {
        int limit = 100;
        return this.myBatisCommonDao.selectList("framework_example.selectTb_Test", limit);
    }





    @Transactional(readOnly = true)
    //DB로 부터 result row를 하나씩 받아와 처리하는 용도 (대용량 결과를 한번에 받기 어려운 경우 또는 result row의 결과를 보고 처리가 필요한 경우 사용)
    public List<Tb_ZipcodeDto> selectListWithResultHandler(){
        MybatisResultHandlerSupport mybatisResultHandlerSupport = new MybatisResultHandlerSupport<Tb_ZipcodeDto, Tb_ZipcodeDto>() {
            int maxCount = 0;

            @Override
            //result row 단위로 해야할 작업을 정의한다.
            public Tb_ZipcodeDto handleResultRow(Tb_ZipcodeDto resultRow) {
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

        return this.myBatisCommonDao.selectListWithResultHandler("framework_example.selectAll", null, mybatisResultHandlerSupport);
    }

    @Transactional(readOnly = true)
    public Map<?, ?> selectMap() {
        int SqlParamForlimit = 3;
        //"컬럼명 c1의 값을 map의 key값으로 하여 Map을 생성한다.
        Map<?, ?> resultMap = this.myBatisCommonDao.selectMap("framework_example.selecWithLimit", SqlParamForlimit, "c1");

        return resultMap;
    }

    @Transactional(readOnly = true)
    //result row의 페이징 처리를 위한 예시
    //파람의 상세 내용은 PageInfoSupport 클레스에서 확인가능
    public PageInfoSupport<Tb_ZipcodeDto> selectPaginate(int currentPageNum, int setRowSizePerPage, int setButtomPageNavigationSize) {
        return this.myBatisCommonDao.selectPaginatedList("_framework_example.selectAll", null,
                currentPageNum, setRowSizePerPage, setButtomPageNavigationSize);
    }




}
