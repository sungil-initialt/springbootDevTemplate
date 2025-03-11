package com.sptek._frameworkWebCore.filter.config;

import com.sptek._frameworkWebCore.annotation.EnableMdcTagging_InMain;
import com.sptek._frameworkWebCore.annotation.EnableNoFilterAndSessionForMinorRequest_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import com.sptek._frameworkWebCore.filter.DetailLogFilterWithAnnotation;
import com.sptek._frameworkWebCore.filter.MakeMdcFilter;
import com.sptek._frameworkWebCore.filter.MakeRequestTimestampFilter;
import com.sptek._frameworkWebCore.filter.NoSessionFilterForMinorRequest;
import com.sptek._frameworkWebCore.springSecurity.CustomJwtFilter;
import com.sptek._frameworkWebCore.springSecurity.GeneralTokenProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.web.context.request.RequestContextListener;

//@Configuration
public class FilterConfigForFrameworkWebCore {
    // todo : 이 빈에 대해 좀더 확인 필요
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Bean
    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_InBean(EnableNoFilterAndSessionForMinorRequest_InMain.class)
    public FilterRegistrationBean<NoSessionFilterForMinorRequest> noSessionFilterForMinorRequest() {
        FilterRegistrationBean<NoSessionFilterForMinorRequest> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new NoSessionFilterForMinorRequest());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Profile(value = { "local", "dev", "stg", "prd" })
    //@HasAnnotationOnMain_InBean(DisableFilterAndSessionForMinorRequest_InMain.class)
    public FilterRegistrationBean<CustomJwtFilter> customJwtFilter(GeneralTokenProvider generalTokenProvider) {
        FilterRegistrationBean<CustomJwtFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new CustomJwtFilter(generalTokenProvider));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE +1);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Bean
    @Profile(value = { "local", "dev", "stg", "prd" })
    @HasAnnotationOnMain_InBean(EnableMdcTagging_InMain.class)
    public FilterRegistrationBean<MakeMdcFilter> makeMdcFilter() {
        FilterRegistrationBean<MakeMdcFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeMdcFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE +2);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Bean
    @Profile(value = { "local", "dev", "stg", "prd" })
    public FilterRegistrationBean<MakeRequestTimestampFilter> makeRequestTimestampFilter() {
        FilterRegistrationBean<MakeRequestTimestampFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new MakeRequestTimestampFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE +3);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

    @Bean
    @Profile(value = { "local", "dev", "stg", "prd" })
    public FilterRegistrationBean<DetailLogFilterWithAnnotation> detailLogFilterWithAnnotation() {
        FilterRegistrationBean<DetailLogFilterWithAnnotation> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new DetailLogFilterWithAnnotation());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE +4);  // Spring 필터 순서 설정
        return filterRegistrationBean;
    }

//    @Profile(value = { "local", "dev", "stg", "prd" })
//    @HasAnnotationOnMain_InBean(EnableCorsPolicyFilter_InMain.class)
//    @DependsOn({"corsPropertiesVo"})
//    @Bean
//    public FilterRegistrationBean<CorsPolicyFilter> corsPolicyFilter(CorsPropertiesVo corsPropertiesVo) {
//        FilterRegistrationBean<CorsPolicyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
//        filterRegistrationBean.setFilter(new CorsPolicyFilter(corsPropertiesVo));
//        filterRegistrationBean.addUrlPatterns("/api/*");
//        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);  // Spring 필터 순서 설정
//        return filterRegistrationBean;
//    }
}
