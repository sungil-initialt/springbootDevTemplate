package com.sptek._projectCommon.interceptor;


import com.sptek._frameworkWebCore.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final ExampleInterceptor exampleInterceptor;


    @Override
    public void addInterceptors(@NotNull InterceptorRegistry interceptorRegistry) {

        //필요한 interceptor 등록 (exampleInterceptor 참고)
        if(exampleInterceptor != null) {
            interceptorRegistry.addInterceptor(this.exampleInterceptor).addPathPatterns("/**")
                    .excludePathPatterns("/api/**")
                    .excludePathPatterns(SecurityUtil.getNotEssentialRequestPatterns())
                    .excludePathPatterns(SecurityUtil.getStaticResourceRequestPatterns());
        }

        WebMvcConfigurer.super.addInterceptors(interceptorRegistry);
    }

}