package com.sptek._frameworkWebCore.datasource;


import com.sptek._frameworkWebCore.annotation.EnableDatasourceOfH2_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

//@Profile(value = { "!prd" }) //prd가 아닐때
@Profile(value = { "local"})
@Slf4j
@HasAnnotationOnMain_InBean(EnableDatasourceOfH2_InMain.class)
@Configuration
public class DataSourceConfigForH2 {

    @Bean(name = "dataSource")
    public DataSource dataSource(
            @Value("${h2.datasource.driverClassName}") String driverClassName
            , @Value("${h2.datasource.url}") String url
            , @Value("${h2.datasource.username}") String username
            , @Value("${h2.datasource.password}") String password) {

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
