package com.sptek._frameworkWebCore.filter.config;

import com.sptek._frameworkWebCore._annotation.Enable_CorsPolicyFilter_At_Main;
import com.sptek._frameworkWebCore._annotation.Enable_MdcTagging_At_Main;
import com.sptek._frameworkWebCore._annotation.Enable_NoFilterAndSessionForMinorRequest_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.filter.*;
import com.sptek._frameworkWebCore.commonObject.vo.CorsPropertiesVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

@Slf4j
@Configuration
public class FilterConfigForFrameworkWebCore {
    // todo: 아래 필터 설정보다 Spring Security Filter Chain 이 항상 우선함


    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_At_Bean(Enable_MdcTagging_At_Main.class)
    @Bean
    public FilterRegistrationBean<MakeMdcFilter> makeMdcFilter() {
        FilterRegistrationBean<MakeMdcFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeMdcFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_At_Bean(Enable_NoFilterAndSessionForMinorRequest_At_Main.class)
    @Bean
    public FilterRegistrationBean<NoSessionFilterForMinorRequest> noSessionFilterForMinorRequest() {
        FilterRegistrationBean<NoSessionFilterForMinorRequest> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new NoSessionFilterForMinorRequest());
        filterRegistrationBean.addUrlPatterns("/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" })
    @Bean
    public FilterRegistrationBean<MakeRequestTimestampFilter> makeRequestTimestampFilter() {
        FilterRegistrationBean<MakeRequestTimestampFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeRequestTimestampFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" })
    @Bean
    public FilterRegistrationBean<DetailLogFilter> detailLogFilterWithAnnotation() {
        FilterRegistrationBean<DetailLogFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new DetailLogFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_At_Bean(Enable_CorsPolicyFilter_At_Main.class)
    @DependsOn({"corsPropertiesVo"})
    @Bean
    public FilterRegistrationBean<CorsPolicyFilter> corsPolicyFilter(CorsPropertiesVo corsPropertiesVo) {
        //log.debug("corsPolicyFilter is applied.");
        FilterRegistrationBean<CorsPolicyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new CorsPolicyFilter(corsPropertiesVo));
        filterRegistrationBean.addUrlPatterns("/api/*");
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }
}
