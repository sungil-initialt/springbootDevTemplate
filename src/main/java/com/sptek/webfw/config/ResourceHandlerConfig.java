package com.sptek.webfw.config;

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
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //js.css 변경에 따른 deploy시 파일 이름을 변경하게 됨으로 관련 maxAge를 길게 가져가도 될듯
        CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365)).cachePublic();

        //html등에서 resource 위치를 축약해서 사용할수 있게 해준다.
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/resources/webjars/");

        //프로퍼티 속성 spring.mvc.static-path-pattern 의 설정과 중복될수 있음(양쪽 설정 모두 적용됨, 그러나 프로퍼티 속성이 없는 경우는 /static 하위를 /**로 매핑한것으로 디포트 설정됨을 주의)
        //registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/").setCacheControl(cacheControl); //todo: css 파일이 케싱되지 않는 이유 확인 필요
        //registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/").setCacheControl(cacheControl);
        //registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/").setCacheControl(cacheControl);
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCacheControl(cacheControl);
    }
}
