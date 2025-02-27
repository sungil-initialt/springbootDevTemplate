package com.sptek._frameworkWebCore.interceptor;


import com.sptek._frameworkWebCore.util.SecurityUtil;
import com.sptek._projectCommon.interceptor.ExampleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfigForSptfw implements WebMvcConfigurer {

    private final ExampleInterceptor exampleInterceptor;
    private final UvCheckInterceptor uvCheckInterceptor;
    private final ViewErrorLogSupportInterceptor viewErrorLogSupportInterceptor;

    //조건에 따라 Interceptor 들이 Bean 으로 등독 될수도 안 될수도 있는 상황이 있기 때문에 @Nullable 사용한 생성자를 직접 구현하였음
    public InterceptorConfigForSptfw(@Nullable ExampleInterceptor exampleInterceptor
            , @Nullable UvCheckInterceptor uvCheckInterceptor
            , @Nullable ViewErrorLogSupportInterceptor viewErrorLogSupportInterceptor) {
        this.exampleInterceptor = exampleInterceptor;
        this.uvCheckInterceptor = uvCheckInterceptor;
        this.viewErrorLogSupportInterceptor = viewErrorLogSupportInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {

        //필요한 interceptor 등록 (exampleInterceptor 참고)
        if(uvCheckInterceptor != null) {
            interceptorRegistry.addInterceptor(this.uvCheckInterceptor).addPathPatterns("/**")
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

//        interceptorRegistry.addInterceptor(new InterceptorConfigSupportForRequestMethod(new ExampleInterceptor())
//                //2차 필터 조건, 아래 GET의 경우 1차 대상에 포함되나 무조건 제외, api/v1 POST는 인정, api/v2 POST는 제외
//                .excludePathPattern("/api/**", HttpMethod.GET)
//                .excludePathPattern("/api/v2/**", HttpMethod.POST)
//                ).addPathPatterns("/api/**").excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns()); //1차 필터 조건

        WebMvcConfigurer.super.addInterceptors(interceptorRegistry);
    }

}