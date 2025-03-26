package com.sptek._frameworkWebCore.webMvcConfigurer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class ViewControllerConfig implements WebMvcConfigurer {

    //실제 viewcontroller를 만들지 않고도 간단한 역할을 수행함
    @Override
    public void addViewControllers(ViewControllerRegistry viewControllerRegistry) {
        //swagger.
        viewControllerRegistry.addRedirectViewController("/api/demo-ui.html", "/demo-ui.html");

        //별도 컨트럴러 매핑 없이 view로 넘어가도록 설정 (이경우 @ControllerAdvice 가 동작 하지 않음을 주의)
        viewControllerRegistry.addViewController("/").setViewName("/pages/example/test/none");
        viewControllerRegistry.addViewController("/none").setViewName("/pages/example/test/none");
        viewControllerRegistry.addViewController("/temporaryParkingPageForXXX").setViewName("/pages/example/test/temporaryParkingView");
        viewControllerRegistry.addViewController("/sorry").setViewName("/pages/example/test/temporaryParkingView");
        viewControllerRegistry.addViewController("/fileUpload").setViewName("/pages/example/test/fileUpload");
        viewControllerRegistry.addViewController("/pageForSubmitTest").setViewName("/pages/example/test/pageForSubmitTest");
        viewControllerRegistry.addViewController("/pageForApiTest").setViewName("/pages/example/test/pageForApiTest");
    }

    /*
    @Override
    public void configureViewResolvers(ViewResolverRegistry viewResolverRegistry) {
        //thymeleaf 설정을 application.yml 에서 설정하고 있어서 사용하지 않도록 처리됨

        //jsp로 설정이 필요한 경우
        viewResolverRegistry.jsp("/WEB-INF/views/", ".jsp");
        WebMvcConfigurer.super.configureViewResolvers(viewResolverRegistry);
    }
    */
}
