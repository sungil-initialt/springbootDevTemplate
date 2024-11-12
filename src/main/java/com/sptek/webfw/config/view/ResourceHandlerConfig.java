package com.sptek.webfw.config.view;

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
        //swagger를 위한 리소스핸들러 설정
        resourceHandlerRegistry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/resources/");
        resourceHandlerRegistry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/resources/webjars/");

        //deploy시 파일(js, css등) 이름을 변경하게 됨으로 관련 캐시의 maxAge를 길게 가져가도 될듯
        CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365)).cachePublic();

        //프로퍼티 속성 spring.web.resources.static-locations의 설정의 역할과 동일, 양쪽에 둘다 설정될수 있음(양쪽 설정 모두 적용됨, 그러나 프로퍼티 속성이 없는 경우는 /static 하위를 /**로 매핑한것으로 디포트 설정됨을 주의)
        resourceHandlerRegistry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCacheControl(cacheControl);
        resourceHandlerRegistry.addResourceHandler("/**").addResourceLocations("classpath:/static/").setCacheControl(cacheControl); //todo: 이렇게 추가해도 문제없을까?
    }
}

