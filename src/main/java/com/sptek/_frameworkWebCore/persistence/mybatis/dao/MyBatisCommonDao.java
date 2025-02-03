package com.sptek._frameworkWebCore.persistence.mybatis.dao;

import com.sptek._frameworkWebCore.support.MybatisResultHandlerSupport;
import com.sptek._frameworkWebCore.support.PageHelperSupport;
import com.sptek._frameworkWebCore.support.PageInfoSupport;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/*
mybatis를 이용한 db 기본 템플릿을 제공함
 */

@SuppressWarnings("rawtypes")
@Slf4j
@Component("myBatisCommonDao")
public class MyBatisCommonDao {

    @Autowired
    @Qualifier("sqlSessionTemplate")
    private SqlSessionTemplate sqlSessionTemplate;

    public <T> T selectOne(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return (T)(this.sqlSessionTemplate.selectOne(statementId, parameter));
    }

    public <T> List<T> selectList(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return  (List<T>) this.sqlSessionTemplate.selectList(statementId, parameter);
    }

    //DB로 부터 result row를 하나씩 받아가며 중간처리 작업을 진행할 수 있게 해준다.
    public <T, R> List<R> selectListWithResultHandler(String statementId, Object parameter,
                                                      final MybatisResultHandlerSupport<T, R> mybatisResultHandlerSupport) {
        log.debug("statementId = {}", statementId);
        final List<R> finalHeandledResults = new ArrayList<R>();
        try {
            mybatisResultHandlerSupport.open();
            this.sqlSessionTemplate.select(statementId, parameter
                    , context -> {
                        R handledResult = mybatisResultHandlerSupport.handleResultRow((T) context.getResultObject());
                        if (handledResult != null) finalHeandledResults.add(handledResult);
                        if (mybatisResultHandlerSupport.isStop()) context.stop();
                    });
        } finally {
            mybatisResultHandlerSupport.close();
        }

        return finalHeandledResults;
    }

    public <T> PageInfoSupport<T> selectPaginatedList(String statementId, @Nullable Object parameter,
                                                      int currentPageNum, int setRowSizePerPage, int setButtomPageNavigationSize) {
        log.debug("statementId = {}", statementId);

        //todo : totla 사이즈를 매번 구하지 않도록 캐싱 방안을 고려해야함
        int defaultSetRowSizePerPage = 20;
        int defaultSetButtomPageNavigationSize = 10;

        currentPageNum = currentPageNum <= 0 ? 1 : currentPageNum;
        setRowSizePerPage = setRowSizePerPage <= 0 ? defaultSetRowSizePerPage : setRowSizePerPage;
        setButtomPageNavigationSize = setButtomPageNavigationSize <= 0 ? defaultSetButtomPageNavigationSize : setButtomPageNavigationSize;

        PageHelperSupport.setPageForSelect(currentPageNum, setRowSizePerPage);
        PageInfoSupport<T> pageInfoSupport = PageHelperSupport.selectPaginatedList((List<T>) this.sqlSessionTemplate.selectList(statementId, parameter), setButtomPageNavigationSize);

        return pageInfoSupport;
    }

    public Map<?, ?> selectMap(String statementId, @Nullable Object parameter, String columnNameForMapkey) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.selectMap(statementId, parameter, columnNameForMapkey);
    }

    public Integer insert(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.insert(statementId, parameter);
    }

    public Integer update(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.update(statementId, parameter);
    }

    public Integer delete(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.delete(statementId, parameter);
    }
}
