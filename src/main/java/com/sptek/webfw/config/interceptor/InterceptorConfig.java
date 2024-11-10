package com.sptek.webfw.config.interceptor;


import com.sptek.webfw.support.MethodCheckInterceptorSupport;
import com.sptek.webfw.util.SecureUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private ExampleInterceptor exampleInterceptor;
    @Autowired
    private RequestInfoInterceptor requestInfoInterceptor;
    @Autowired
    private ResponseInfoInterceptor responseInfoInterceptor;
    @Autowired
    private UvInterceptor uvInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {

        //필요한 interceptor 등록 (exampleInterceptor 참고)
        //registry.addInterceptor(this.exampleInterceptor).addPathPatterns("/**").excludePathPatterns(getNotEssentialRequestPatterns()).excludePathPatterns(SecureUtil.getStaticResourceRequestPatternList());
        interceptorRegistry.addInterceptor(this.uvInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/api/**")
                .excludePathPatterns(SecureUtil.getNotEssentialRequestPatterns())
                .excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns());

        interceptorRegistry.addInterceptor(this.requestInfoInterceptor).addPathPatterns("/**")
                .excludePathPatterns(SecureUtil.getNotEssentialRequestPatterns())
                .excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns());

        interceptorRegistry.addInterceptor(this.responseInfoInterceptor).addPathPatterns("/**")
                .excludePathPatterns(SecureUtil.getNotEssentialRequestPatterns())
                .excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns());

        interceptorRegistry.addInterceptor(new MethodCheckInterceptorSupport(new MethodCheckInterceptorForXX())
                //2차 필터 조건, 아래 GET의 경우 1차 대상에 포함되나 무조건 제외, api/v1 POST는 인정, api/v2 POST는 제외
                .excludePathPattern("/api/**", HttpMethod.GET)
                .excludePathPattern("/api/v2/**", HttpMethod.POST)
                ).addPathPatterns("/api/**").excludePathPatterns(SecureUtil.getStaticResourceRequestPatterns()); //1차 필터 조건

        WebMvcConfigurer.super.addInterceptors(interceptorRegistry);
    }

}