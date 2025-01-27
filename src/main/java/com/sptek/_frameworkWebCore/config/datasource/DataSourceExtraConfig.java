package com.sptek._frameworkWebCore.config.datasource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class DataSourceExtraConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager();
        dataSourceTransactionManager.setDataSource(dataSource);

        //트렌젝션 정책 설정
        dataSourceTransactionManager.setGlobalRollbackOnParticipationFailure(false);
        return dataSourceTransactionManager;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);

        // todo: 클레스페스에서는 로딩이 안되는 이유가 뭐지?? (resource 쪽에서만 됨, 일단은 resource 쪽에 위치 시킴)
        sqlSessionFactoryBean.setConfigLocation(this.applicationContext.getResources("classpath*:/**/mapper/*-config.xml")[0]);

        // 위 config.xml 을 통한 설정이 아니라 코딩으로 설정 가능
        // org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // configuration.setMapUnderscoreToCamelCase(true);
        // configuration.setJdbcTypeForNull(JdbcType.NULL);
        // sqlSessionFactoryBean.setConfiguration(configuration);

        sqlSessionFactoryBean.setMapperLocations(this.applicationContext.getResources("classpath*:/**/mapper/*Mapper.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}