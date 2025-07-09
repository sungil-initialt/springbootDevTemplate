package com.sptek._frameworkWebCore.interceptor.config;


import com.sptek._frameworkWebCore.interceptor.ModelAndViewXssProtectInterceptor;
import com.sptek._frameworkWebCore.interceptor.UvCheckLogInterceptor;
import com.sptek._frameworkWebCore.interceptor.ViewErrorLogSupportInterceptor;
import com.sptek._frameworkWebCore.util.SecurityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorGlobalConfig implements WebMvcConfigurer {

    private final UvCheckLogInterceptor uvCheckLogInterceptor;
    private final ViewErrorLogSupportInterceptor viewErrorLogSupportInterceptor;
    private final ModelAndViewXssProtectInterceptor modelAndViewXssProtectInterceptor;

    //조건에 따라 Interceptor 들이 Bean 으로 등독 될수도 안 될수도 있는 상황이 있기 때문에 @Nullable 을 사용한 생성자 를 직접 구현 하였음
    public InterceptorGlobalConfig(@Nullable UvCheckLogInterceptor uvCheckLogInterceptor
            , @Nullable ViewErrorLogSupportInterceptor viewErrorLogSupportInterceptor
            , @Nullable ModelAndViewXssProtectInterceptor modelAndViewXssProtectInterceptor) {
        this.uvCheckLogInterceptor = uvCheckLogInterceptor;
        this.viewErrorLogSupportInterceptor = viewErrorLogSupportInterceptor;
        this.modelAndViewXssProtectInterceptor = modelAndViewXssProtectInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {

        //필요한 interceptor 등록 (exampleInterceptor 참고)
        if(uvCheckLogInterceptor != null) {
            interceptorRegistry.addInterceptor(this.uvCheckLogInterceptor).addPathPatterns("/**")
                    .excludePathPatterns("/api/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

        if(viewErrorLogSupportInterceptor != null) {
            interceptorRegistry.addInterceptor(this.viewErrorLogSupportInterceptor).addPathPatterns("/**")
                    .excludePathPatterns("/api/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

        if(modelAndViewXssProtectInterceptor != null) {
            interceptorRegistry.addInterceptor(this.modelAndViewXssProtectInterceptor).addPathPatterns("/**")
                    .excludePathPatterns("/api/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

//        interceptorRegistry.addInterceptor(new InterceptorConfigSupportForRequestMethod(new ExampleInterceptor())
//                //2차 필터 조건, 아래 GET의 경우 1차 대상에 포함되나 무조건 제외, api/v1 POST는 인정, api/v2 POST는 제외
//                .excludePathPattern("/api/**", HttpMethod.GET)
//                .excludePathPattern("/api/v2/**", HttpMethod.POST)
//                ).addPathPatterns("/api/**").excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns()); //1차 필터 조건

        WebMvcConfigurer.super.addInterceptors(interceptorRegistry);
    }

}