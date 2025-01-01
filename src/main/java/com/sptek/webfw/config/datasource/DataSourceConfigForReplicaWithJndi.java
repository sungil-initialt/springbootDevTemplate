package com.sptek.webfw.config.datasource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Profile(value = { "prd" })
@RequiredArgsConstructor
//todo: JNDI 방식에 대한 테스트 필요
public class DataSourceConfigForReplicaWithJndi {


    @Value("${jndi.datasource.lookup.write.name}") //프로퍼티 항목 정의
    private String jndiWriteDatasourceLookupName;
    @Value("${jndi.datasource.lookup.read.name}")
    private String jndiReadDatasourceLookupName;


    @Bean(name = "writeDataSource", destroyMethod = "")
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
    // DataSource 가 여럿 존재할수 있기 때문에 @Qualifier통해 그 중 명확한 이름으로 선언된 것을 주입해 줄수 있다.
    // write, read를 나눠 사용할수 있도록 ReplicationRoutingDataSource 생성
    public DataSource routingDataSource(@Qualifier("writeDataSource") DataSource writeDataSource,
                                        @Qualifier("readDataSource") DataSource readDataSource) {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        Map<Object, Object> dataSources = new HashMap<Object, Object>();
        dataSources.put("write", writeDataSource);
        dataSources.put("read", readDataSource);
        routingDataSource.setTargetDataSources(dataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);

        return routingDataSource;
    }

    @Bean(name = "dataSource")
    @DependsOn({"routingDataSource"})
    // 실제 spring이 dataSource를 찾을때 ReplicationRoutingDataSource를 내부적으로 사용하는 LazyConnectionDataSourceProxy를 반환함.
    public DataSource routingLazyDataSource(@Qualifier("routingDataSource") DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
        //@Transactional(readOnly = true) 를 사용하는 경우 read용 dataSource를 활용하도록 처리함으로써 속도 계선 가능.

        @Override
        protected Object determineCurrentLookupKey() {
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            return isReadOnly ? "read" : "write";
        }
    }
}
