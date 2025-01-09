package com.sptek.webfw.config.interceptor;


import com.sptek.webfw.util.SecureUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final ExampleInterceptor exampleInterceptor;
    private final UvInterceptor uvInterceptor;
    private final ReqResLogFilterSupportInterceptor reqResLogFilterSupportInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {

        //필요한 interceptor 등록 (exampleInterceptor 참고)
        //registry.addInterceptor(this.exampleInterceptor).addPathPatterns("/**").excludePathPatterns(getNotEssentialRequestPatterns()).excludePathPatterns(SecureUtil.getStaticResourceRequestPatternList());
        interceptorRegistry.addInterceptor(this.uvInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/api/**")
                .excludePathPatterns(SecureUtil.getNotEssentialRequestPatterns())
                .excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns());

        interceptorRegistry.addInterceptor(this.reqResLogFilterSupportInterceptor).addPathPatterns("/**")
                .excludePathPatterns(SecureUtil.getNotEssentialRequestPatterns())
                .excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns());

        //interceptorRegistry.addInterceptor(new MethodCheckInterceptorSupport(new MethodCheckInterceptorForXX())
        //        //2차 필터 조건, 아래 GET의 경우 1차 대상에 포함되나 무조건 제외, api/v1 POST는 인정, api/v2 POST는 제외
        //        .excludePathPattern("/api/**", HttpMethod.GET)
        //        .excludePathPattern("/api/v2/**", HttpMethod.POST)
        //        ).addPathPatterns("/api/**").excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns()); //1차 필터 조건

        WebMvcConfigurer.super.addInterceptors(interceptorRegistry);
    }

}