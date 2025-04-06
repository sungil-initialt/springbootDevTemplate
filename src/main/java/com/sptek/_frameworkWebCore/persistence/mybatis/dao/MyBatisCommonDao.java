package com.sptek._frameworkWebCore.persistence.mybatis.dao;

import com.sptek._frameworkWebCore.support.MybatisResultHandlerSupport;
import com.sptek._frameworkWebCore.support.PageHelperSupport;
import com.sptek._frameworkWebCore.support.PageInfoSupport;
import com.sptek._frameworkWebCore.util.SpringUtil;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
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

    public <T> T selectOne(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return (T)(this.sqlSessionTemplate.selectOne(statementId, parameter));
    }

    public <T> List<T> selectList(String statementId, @Nullable Object parameter) {
        log.debug("statementId = {}", statementId);
        return  (List<T>) this.sqlSessionTemplate.selectList(statementId, parameter);
    }

    public Map<?, ?> selectMap(String statementId, @Nullable Object parameter, String columnNameForMapkey) {
        log.debug("statementId = {}", statementId);
        return this.sqlSessionTemplate.selectMap(statementId, parameter, columnNameForMapkey);
    }

    // DB로 부터 result row를 하나씩 받아가며 중간처리 작업을 진행할 수 있게 해준다.
    // 조회 범위를 러프하게 잡고 원하는 요소만 모을수 있다, 메모리 절약가능, 반대로 DB 커넥션을 잡고 있음, 커넥션 타임아웃 주의
    public <T, R> List<R> selectListWithResultHandler(
            String statementId, Object parameter,
            final MybatisResultHandlerSupport<T, R> mybatisResultHandlerSupport)
    {
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
    
    public <T> PageInfoSupport<T> selectListWithPagination(String statementId, @Nullable Object parameter)
    {
        log.debug("statementId = {}", statementId);

        //이부분 해결해야 함!!!
        //todo : 전체 row 의 total size 를 매번 구하지 않도록 캐싱 방안을 고려 해야함
        int maxSetRowSizePerPage = 100;
        int defaultSetRowSizePerPage = 20;
        int defaultSetBottomPageNavigationSize = 10;

        HttpServletRequest httpServletRequest = SpringUtil.getRequest();
        int currentPageNum = httpServletRequest.getParameter("currentPageNum") != null
                ? Integer.parseInt(httpServletRequest.getParameter("currentPageNum"))
                : 0;
        currentPageNum = currentPageNum <= 0 ? 1 : currentPageNum;

        int setRowSizePerPage = httpServletRequest.getParameter("setRowSizePerPage") != null
                ? Integer.parseInt(httpServletRequest.getParameter("setRowSizePerPage"))
                : 0;
        setRowSizePerPage = setRowSizePerPage <= 0 ? defaultSetRowSizePerPage : setRowSizePerPage;
        setRowSizePerPage = Math.min(setRowSizePerPage, maxSetRowSizePerPage);

        int setBottomPageNavigationSize = httpServletRequest.getParameter("setBottomPageNavigationSize") != null
                ? Integer.parseInt(httpServletRequest.getParameter("setBottomPageNavigationSize"))
                : 0;
        setBottomPageNavigationSize = setBottomPageNavigationSize <= 0 ? defaultSetBottomPageNavigationSize : setBottomPageNavigationSize;

        //setPageForSelect 내부 에서 PageHelper.startPage 가 호출 되면서 다음 mybatis 쿼리에 자동 으로 limit 처리를 해줌
        PageHelperSupport.setPageForSelect(currentPageNum, setRowSizePerPage);
        PageInfoSupport<T> pageInfoSupport;
        if (parameter == null) {
            pageInfoSupport = PageHelperSupport.selectPaginatedList(this.sqlSessionTemplate.selectList(statementId), setBottomPageNavigationSize);
        } else {
            pageInfoSupport = PageHelperSupport.selectPaginatedList(this.sqlSessionTemplate.selectList(statementId, parameter), setBottomPageNavigationSize);
        }
        return pageInfoSupport;
    }


}
