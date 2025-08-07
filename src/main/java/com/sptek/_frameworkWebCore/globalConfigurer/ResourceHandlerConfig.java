package com.sptek._frameworkWebCore.globalConfigurer;

import com.sptek._frameworkWebCore._annotation.Enable_HttpCachePublicForStaticResource_At_Main;
import com.sptek._frameworkWebCore._annotation.annotationCondition.HasAnnotationOnMain_At_Bean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.springframework.web.servlet.resource.VersionResourceResolver;

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

    @HasAnnotationOnMain_At_Bean(Enable_HttpCachePublicForStaticResource_At_Main.class)
    @Configuration
    public class EnableHttpCachePublicForStaticResource implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {

            // 프로퍼티 속성 spring.web.resources.static-locations의 설정의 역할과 동일,
            // 양쪽에 둘다 설정 될수 있음(양쪽 설정 모두 적용됨, 그러나 프로퍼티 속성이 없는 경우는 /static 하위를 /**로 매핑한것으로 디포트 설정됨을 주의)
            // VersionResourceResolver 의 경우 thymeleaf 내에서만 동작함으로
            // 그럼으로 thymeleaf 경로 밖의 예를 들어 /static/js/ js파일 내부에서 다른 js 파일을 import 하는 경우 적용이 안됨(cache busting 에 주의)
            resourceHandlerRegistry.addResourceHandler("/**")
                    .addResourceLocations("classpath:/static/") //todo: 참고-static 파일이 변경된 경우는 서버 재시작을 해야 캐시값이 새로 적용됨
                    .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic())
                    .resourceChain(true)
                    .addResolver(new VersionResourceResolver().addContentVersionStrategy("/**"));

            // todo: 아래와 같이 리소스 핸들러 경로에 프리픽스(/static/)를 주면 리소스 인식에는 문제가 없는데..
            //  VersionResourceResolver가 적용되지 않는(리소스에 해싱값이 안붙음) 현상이 있음 (원인 확인 필요), thymeleaf 버그 일수도..
            //  resourceHandlerRegistry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/").setCacheControl(cacheControl);
        }

        @Bean
        //static resource 의 버전(해싱값) 처리를 위해 필요
        public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
            return new ResourceUrlEncodingFilter();
        }

    }

    @HasAnnotationOnMain_At_Bean(value = Enable_HttpCachePublicForStaticResource_At_Main.class, negate = true)
    @Configuration
    public class DisableHttpCachePublicForStaticResource implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry resourceHandlerRegistry) {
            resourceHandlerRegistry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        }
    }
}

