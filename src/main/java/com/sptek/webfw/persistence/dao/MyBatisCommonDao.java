package com.sptek.webfw.persistence.dao;


import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
@Slf4j
@Component("myBatisCommonDao")
public class MyBatisCommonDao {


    @Autowired
    @Qualifier("sqlSessionTemplate")
    protected SqlSessionTemplate sqlSessionTemplate;

    public <T> T selectOne(String statementId, @Nullable Object parameter, Class<T> clazz) {
        return clazz.cast(this.sqlSessionTemplate.selectOne(statementId, parameter));
    }

    public <T> List<T> selectList(String statementId, @Nullable Object parameter, Class<T> clazz) {
        List<T> list = (List<T>) this.sqlSessionTemplate.selectList(statementId, parameter);
        return list;
    }

    public Map<?, ?> selectMap(String statementId, @Nullable Object parameter, String columnNameForMapkey) {
        return this.sqlSessionTemplate.selectMap(statementId, parameter, columnNameForMapkey);
    }

    public Integer insert(String statementId, @Nullable Object parameter) {
        return this.sqlSessionTemplate.insert(statementId, parameter);
    }

    public Integer update(String statementId, @Nullable Object parameter) {
        return this.sqlSessionTemplate.update(statementId, parameter);
    }

    public Integer delete(String statementId, @Nullable Object parameter) {
        return this.sqlSessionTemplate.delete(statementId, parameter);
    }
}
