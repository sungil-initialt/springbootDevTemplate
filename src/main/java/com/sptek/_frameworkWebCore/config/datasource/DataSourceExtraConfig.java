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

        // java 와 동일한 패키지 않에 xml 을 넗는 방식은 일반적이지 않으며 굳이 한다면 xml 위치의 패키지를 ClassPathResource 로 설정해줘야 함
        sqlSessionFactoryBean.setConfigLocation(this.applicationContext.getResources("classpath:/_projectCommonResources/mybatis/*-config.xml")[0]);
        // 위 config.xml 을 통한 설정이 아니라 코딩으로 설정 가능
        // org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // configuration.setMapUnderscoreToCamelCase(true);
        // configuration.setJdbcTypeForNull(JdbcType.NULL);
        // sqlSessionFactoryBean.setConfiguration(configuration);

        sqlSessionFactoryBean.setMapperLocations(this.applicationContext.getResources("classpath:/_projectCommonResources/mybatis/mapper/*Mapper.xml"));
        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}