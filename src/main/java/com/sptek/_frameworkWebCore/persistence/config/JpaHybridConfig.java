package com.sptek._frameworkWebCore.persistence.config;

import com.sptek._frameworkWebCore.annotation.EnableJpaHybrid_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@HasAnnotationOnMain_InBean(EnableJpaHybrid_InMain.class)
@Configuration
public class JpaHybridConfig implements WebMvcConfigurer {

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(localContainerEntityManagerFactoryBean.getObject());

        // todo : 트렌젝션 정책 설정은 상황에 맞게 조절 필요!!
        jpaTransactionManager.setGlobalRollbackOnParticipationFailure(false);
        return jpaTransactionManager;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource);
        // todo : 다른 패키지도 스캔할수 있도록 설정 필요
        localContainerEntityManagerFactoryBean.setPackagesToScan("com.sptek._frameworkWebCore.springSecurity.extras.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(vendorAdapter);

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update"); //(create, create-drop, update, none)
        properties.put("hibernate.show_sql", "false");
        localContainerEntityManagerFactoryBean.setJpaPropertyMap(properties);

        return localContainerEntityManagerFactoryBean;
    }


}