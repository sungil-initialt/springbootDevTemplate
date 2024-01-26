package com.sptek.webfw.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Profile(value = { "prd" })
//todo todcat JNDI 방식에 대해 실제 테스트는 해보지 않았음
public class DataSourceConfigWithJndi extends DataSourceConfigWithProperty{
    @Autowired
    private ApplicationContext applicationContext;
    @Value("${jndi.datasource.lookup.write.name}") //프로퍼티 항목 정의
    private String jndiWriteDatasourceLookupName;
    @Value("${jndi.datasource.lookup.read.name}")
    private String jndiReadDatasourceLookupName;


    @Bean(name = "writeDataSource", destroyMethod = "")
    //tomcat에 설정한 jndi 값을 통해 datasource 정보를 읽어온다.
    //Spring이 DataSource를 필요로하는 시점에 여러개가 존재할수 있기때문에 별도의 이름을 추가해 줄수 있다. (기본은 return 타입)
    //Spring이 bean 소멸시 자동으로 dataSource의 close를 기본으로 호출해줌으로 destroyMethod를 따로 선언하지 않아도 된다(필요한경우 사용)
    // write용 리프리케이션.
    public org.apache.tomcat.jdbc.pool.DataSource writeDataSource() {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        org.apache.tomcat.jdbc.pool.DataSource dataSource = (org.apache.tomcat.jdbc.pool.DataSource) dataSourceLookup
                .getDataSource(this.jndiWriteDatasourceLookupName);
        return dataSource;
    }

    @Bean(name = "readDataSource", destroyMethod = "")
    public org.apache.tomcat.jdbc.pool.DataSource readDataSource() {
        JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
        org.apache.tomcat.jdbc.pool.DataSource dataSource = (org.apache.tomcat.jdbc.pool.DataSource) dataSourceLookup
                .getDataSource(this.jndiReadDatasourceLookupName);
        return dataSource;
    }

    @Bean(name = "routingDataSource")
    //DataSource 가 여럿 존재할수 있기 때문에 @Qualifier통해 그 중 명확한 이름으로 선언된 것을 주입해 줄수 있다.
    //write, read를 나눠 사용할수 있도록 ReplicationRoutingDataSource 생성
    public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
                                        @Qualifier("readDataSource") DataSource readDataSource) {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
        dataSourceMap.put("write", writeDataSource);
        dataSourceMap.put("read", readDataSource);
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    @Bean(name = "dataSource")
    @DependsOn({"routingDataSource"})
    //실제 spring이 dataSource를 찾을때 ReplicationRoutingDataSource를 내부적으로 사용하는 LazyConnectionDataSourceProxy를 반환함.
    public DataSource routingLazyDataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        transactionManager.setGlobalRollbackOnParticipationFailure(false); //TODO
        return transactionManager;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setConfigLocation(this.applicationContext.getResources("classpath*:/**/mapper/*config.xml")[0]); //인덱스값을 수정하여 상황에 맞게 구성된 값을 로딩할 수 있다.

        //위 config.xml 을 통한 설정이 아니라 코딩으로 설정 가능
        //org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        //configuration.setMapUnderscoreToCamelCase(true);
        //configuration.setJdbcTypeForNull(JdbcType.NULL);
        //sessionFactoryBean.setConfiguration(configuration);

        sessionFactoryBean.setMapperLocations(this.applicationContext.getResources("classpath*:/**/mapper/*Mapper.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "sqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory);
        return sqlSessionTemplate;
    }

    public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
        @Override
        //@Transactional(readOnly = true) 를 사용하는 경우 read용 dataSource를 활용하도록 처리함으로써 속도 계선 가능.
        protected Object determineCurrentLookupKey() {
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            return isReadOnly ? "read" : "write";
        }
    }
}
