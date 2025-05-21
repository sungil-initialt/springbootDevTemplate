package com.sptek._frameworkWebCore.filter.config;

import com.sptek._frameworkWebCore.annotation.EnableCorsPolicyFilter_InMain;
import com.sptek._frameworkWebCore.annotation.EnableMdcTagging_InMain;
import com.sptek._frameworkWebCore.annotation.EnableNoFilterAndSessionForMinorRequest_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import com.sptek._frameworkWebCore.filter.*;
import com.sptek._frameworkWebCore.globalVo.CorsPropertiesVo;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

@Configuration
public class FilterConfigForFrameworkWebCore {
    // todo: 아래 필터 설정보다 Spring Security Filter Chain 이 항상 우선함

    @Bean
    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_InBean(EnableMdcTagging_InMain.class)
    public FilterRegistrationBean<MakeMdcFilter> makeMdcFilter() {
        FilterRegistrationBean<MakeMdcFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeMdcFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Bean
    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_InBean(EnableNoFilterAndSessionForMinorRequest_InMain.class)
    public FilterRegistrationBean<NoSessionFilterForMinorRequest> noSessionFilterForMinorRequest() {
        FilterRegistrationBean<NoSessionFilterForMinorRequest> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new NoSessionFilterForMinorRequest());
        filterRegistrationBean.addUrlPatterns("/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Bean
    @Profile(value = { "local", "dev", "stg", "prd" })
    public FilterRegistrationBean<MakeRequestTimestampFilter> makeRequestTimestampFilter() {
        FilterRegistrationBean<MakeRequestTimestampFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeRequestTimestampFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Bean
    @Profile(value = { "local", "dev", "stg", "prd" })
    public FilterRegistrationBean<DetailLogFilter> detailLogFilterWithAnnotation() {
        FilterRegistrationBean<DetailLogFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new DetailLogFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_InBean(EnableCorsPolicyFilter_InMain.class)
    @DependsOn({"corsPropertiesVo"})
    @Bean
    public FilterRegistrationBean<CorsPolicyFilter> corsPolicyFilter(CorsPropertiesVo corsPropertiesVo) {
        FilterRegistrationBean<CorsPolicyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new CorsPolicyFilter(corsPropertiesVo));
        filterRegistrationBean.addUrlPatterns("/api/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }
}
