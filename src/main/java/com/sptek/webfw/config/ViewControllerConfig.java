package com.sptek.webfw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class ViewControllerConfig implements WebMvcConfigurer {

    //실제 viewcontroller를 만들지 않고도 간단한 역할을 수행함
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //swagger.
        registry.addRedirectViewController("/api/demo-ui.html", "/demo-ui.html");

        //별도 컨트럴러 매핑 없이 view로 넘어가도록 설정
        registry.addViewController("/").setViewName("/pages/example/test/none");
        registry.addViewController("/none").setViewName("/pages/example/test/none");
        registry.addViewController("/temporaryParkingPageForXXX").setViewName("/pages/example/test/temporaryParkingView");
        registry.addViewController("/sorry").setViewName("/pages/example/test/temporaryParkingView");
        registry.addViewController("/fileUpload").setViewName("/pages/example/test/fileUpload");
        registry.addViewController("/pageWithPost").setViewName("/pages/example/test/pageWithPost");
        registry.addViewController("/apiWithAjax").setViewName("/pages/example/test/apiWithAjax");

    }

    /*
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        //thymeleaf 설정을 application.yml 에서 설정하고 있어서 사용하지 않도록 처리됨

        //jsp로 설정이 필요한 경우
        registry.jsp("/WEB-INF/views/", ".jsp");
        WebMvcConfigurer.super.configureViewResolvers(registry);
    }
    */
}
