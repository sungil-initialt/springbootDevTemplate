package com.sptek._frameworkWebCore._example.api.database;

import com.sptek._frameworkWebCore._example.dto.TbTestDto;
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

public class DatabaseService {
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
    public int insertTbTest(TbTestDto tbTestDto) {
        return this.myBatisCommonDao.insert("framework_example.insertTbTest", tbTestDto);
    }

    @Transactional(readOnly = false)
    public int updateTbTest(TbTestDto tbTestDto) {
        return this.myBatisCommonDao.update("framework_example.updateTbTest", tbTestDto);
    }

    @Transactional(readOnly = false)
    public int deleteTbTest() {
        return this.myBatisCommonDao.delete("framework_example.deleteTbTest", null);
    }

    @Transactional(readOnly = true)
    public TbTestDto getOneTbTest() {
        int limit = 1;
        return this.myBatisCommonDao.selectOne("framework_example.selectTbTestWithLimit", limit);
    }

    @Transactional(readOnly = true)
    public List<TbTestDto> getListTbTest() {
        int limit = 100;
        return this.myBatisCommonDao.selectList("framework_example.selectTbTestWithLimit", limit);
    }

    @Transactional(readOnly = true)
    //DB로 부터 result row를 하나씩 받아와 처리하는 용도 (대용량 결과를 한번에 받기 어려운 경우 또는 result row의 결과를 보고 처리가 필요한 경우 사용)
    public List<TbTestDto> getListTbTestWithResultHandler(){
        MybatisResultHandlerSupport<TbTestDto, TbTestDto> mybatisResultHandlerSupport = new MybatisResultHandlerSupport<>() {
            int maxCount = 0;

            @Override
            //result row 단위로 해야할 작업을 정의 한다. (ex: 특정 조건에 맞는 값이 몇건 수집 되면 종료 처리)
            public TbTestDto handleResultRow(TbTestDto resultRow) {

                if (Integer.parseInt(String.valueOf(resultRow.getC1())) < 2133368224) {
                    log.debug("maxCount = {}, {} was excepted", maxCount, resultRow.getC1());
                    return null; //해당 row 는 제외
                } else {
                    maxCount++;
                    log.debug("maxCount = {}, {} was added", maxCount, resultRow.getC1());
                }

                if(maxCount == 2) {
                    stop(); // 더이상 처리 하지 않음 (현재 row 는 포함됨)
                }

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

        return this.myBatisCommonDao.selectListWithResultHandler("framework_example.selectAllTbTest", null, mybatisResultHandlerSupport);
    }

    @Transactional(readOnly = true)
    public Map<?, ?> getMapTbTest() {
        //"컬럼명 c1의 값을 map의 key값으로 하여 Map을 생성한다.
        int limit = 3;
        return this.myBatisCommonDao.selectMap("framework_example.selectTbTestWithLimit", limit, "c1");
    }

    @Transactional(readOnly = true)
    //result row의 페이징 처리를 위한 예시
    //파람의 상세 내용은 PageInfoSupport 클레스에서 확인가능
    public PageInfoSupport<TbTestDto> getListTbTestWithPagination() {
        return this.myBatisCommonDao.selectListWithPagination("framework_example.selectAllTbTest", null);
    }

}
