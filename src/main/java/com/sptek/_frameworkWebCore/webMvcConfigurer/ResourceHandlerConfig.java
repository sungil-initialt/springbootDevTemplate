package com.sptek._frameworkWebCore.webMvcConfigurer;

import com.sptek._frameworkWebCore.annotation.EnableCachePublicForStaticResourceInMain_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Slf4j
@Configuration
public class ResourceHandlerConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
        // Web static 리소스에 대한 설정은 프로퍼티 spring.web.resources.static-locations: 를 통해서도 설정 가능
        // static-locations: #static resource 에 대한 디폴트 경로 및 표기를 지정함(여러 모양으로 지정가능), 설정이 없다면 resource/static/xxx 를 /xxx로 접근할수 있음
        // - classpath:/resources/ #/resource/static/xxx 를 /xxx로 접근함 (디폴트 설정과 같은 내용)
        // - classpath:/static/ #/resource/static/xxx 를 /static/xxx로 접근함

        //swagger를 위한 리소스핸들러 설정
        resourceHandlerRegistry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/resources/");
        resourceHandlerRegistry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/resources/webjars/");
    }

    @HasAnnotationOnMain_InBean(EnableCachePublicForStaticResourceInMain_InMain.class)
    @Configuration
    public class ResourceHandlerConfigForEnableStaticCache implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
            //deploy시 파일(js, css등) 이름을 변경하게 됨으로 관련 캐시의 maxAge를 길게 가져가도 될듯
            CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365)).cachePublic();
            //프로퍼티 속성 spring.web.resources.static-locations의 설정의 역할과 동일, 양쪽에 둘다 설정될수 있음(양쪽 설정 모두 적용됨, 그러나 프로퍼티 속성이 없는 경우는 /static 하위를 /**로 매핑한것으로 디포트 설정됨을 주의)
            resourceHandlerRegistry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCacheControl(cacheControl);
            resourceHandlerRegistry.addResourceHandler("/**").addResourceLocations("classpath:/static/").setCacheControl(cacheControl);
        }
    }

    @HasAnnotationOnMain_InBean(value = EnableCachePublicForStaticResourceInMain_InMain.class, negate = true)
    @Configuration
    public class ResourceHandlerConfigForForDisableStaticCache implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
            resourceHandlerRegistry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
            resourceHandlerRegistry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        }
    }
}

