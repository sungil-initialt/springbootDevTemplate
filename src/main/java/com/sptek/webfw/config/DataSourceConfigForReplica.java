package com.sptek.webfw.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@Profile(value = { "dev", "stg" })
public class DataSourceConfigForReplica {
    @Bean(name = "writeDataSource", destroyMethod = "")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    //HikariCP를 이용하여 datasource를 관리한다.
    //Spring이 DataSource를 필요로하는 시점에 여러개가 존재할수 있기때문에 별도의 이름을 추가해 줄수 있다. (기본은 return 타입)
    //Spring이 bean 소멸시 자동으로 dataSource의 close를 기본으로 호출해줌으로 destroyMethod를 따로 선언하지 않아도 된다(필요한경우 사용)
    // write용 리프리케이션.
    public DataSource writeDataSource() throws Exception {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "readDataSource", destroyMethod = "")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    public DataSource readDataSource() throws Exception {
        return DataSourceBuilder
                .create()
                .type(HikariDataSource.class)
                .build();
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

    public class ReplicationRoutingDataSource extends AbstractRoutingDataSource {
        @Override
        //@Transactional(readOnly = true) 를 사용하는 경우 read용 dataSource를 활용하도록 처리함으로써 속도 계선 가능.
        protected Object determineCurrentLookupKey() {
            boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
            return isReadOnly ? "read" : "write";
        }
    }
}
