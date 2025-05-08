package com.sptek._frameworkWebCore.applicationContextInitializer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextInitializerFor implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.debug("📌 ApplicationContext 초기화 중!");
        //System.setProperty("springdoc.api-docs.enabled", "false");
    }
}