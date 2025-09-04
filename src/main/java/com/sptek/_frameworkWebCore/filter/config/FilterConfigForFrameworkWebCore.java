package com.sptek._frameworkWebCore.filter.config;

import com.sptek._frameworkWebCore._annotation.Enable_CorsPolicyFilter_At_Main;
import com.sptek._frameworkWebCore._annotation.Enable_MdcTagging_At_Main;
import com.sptek._frameworkWebCore._annotation.Enable_NoFilterAndSessionForMinorRequest_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import com.sptek._frameworkWebCore.commonObject.vo.CorsPropertiesVo;
import com.sptek._frameworkWebCore.filter.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.web.filter.RequestContextFilter;

@Slf4j
@Configuration
public class FilterConfigForFrameworkWebCore {
    // todo: 아래 필터 설정보다 Spring Security Filter Chain 이 항상 우선함

    @Profile(value = { "local", "dev", "stg", "prd" })
    @Bean
    // todo : 아래 custom 필터 내부에서도 RequestContextHolder 를 통해 정보를 사용할수 있도록 하기 위해 우선 순위를 높여 설정함
    // 간혹 필터 내부에서 RequestContextHolder 사용시 셋팅 이전 시점이 있을수 있기때문 (async 리 디스패치 시점에 그런 걍향이 있음)
    // 가장 좋은 방법은 필터 레이어에서는 RequestContextHolder 를 직접 사용하지 않는 것이 좋음
    public FilterRegistrationBean<RequestContextFilter> requestContextFilter() {
        FilterRegistrationBean<RequestContextFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new org.springframework.web.filter.RequestContextFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setAsyncSupported(true);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서를 높게 설정
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_At_Bean(Enable_MdcTagging_At_Main.class)
    @Bean
    public FilterRegistrationBean<MakeMdcFilter> makeMdcFilter() {
        FilterRegistrationBean<MakeMdcFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeMdcFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setAsyncSupported(true);
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
        filterRegistrationBean.setAsyncSupported(true);
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" })
    @Bean
    public FilterRegistrationBean<MakeRequestTimestampFilter> makeRequestTimestampFilter() {
        FilterRegistrationBean<MakeRequestTimestampFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeRequestTimestampFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setAsyncSupported(true);
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        //filterRegistrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" }) // 필터 내부에서 동작을 여부가 다시 한번 결정됨
    @Bean
    public FilterRegistrationBean<ReqResDetailLogFilter> detailLogFilterWithAnnotation() {
        FilterRegistrationBean<ReqResDetailLogFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ReqResDetailLogFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setAsyncSupported(true);
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        //filterRegistrationBean.setDispatcherTypes(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR));
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
        filterRegistrationBean.setAsyncSupported(true);
        //filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }
}
