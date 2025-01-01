package com.sptek.webfw.support;

import com.sptek.webfw.util.SpringUtil;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class ApplicationEventSupport {

    @EventListener
    // 애플리케이션 컨텍스트가 초기화되거나 새로고침될 때 실행
    public void handleContextRefresh(ContextRefreshedEvent event) {
        //test for LogbackKeywordFilterForXXX, this below info log will be saved in specific file.
        log.info("XXX >> systemStart: message here!");

        //현재 프로파일 확인
        final Environment environment = event.getApplicationContext().getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();
        log.debug("activeProfiles = {}", Arrays.toString(activeProfiles));

        //시스템 설정 프로퍼티 모두 노출(시스템 설정상의 문제를 확인하는데 도움을 줄 수 있다)
        /*
        if(Boolean.parseBoolean(environment.getProperty("system.propertySources.expose-enabled"))) {
            final MutablePropertySources mutablePropertySources = ((AbstractEnvironment) environment).getPropertySources();
            StreamSupport.stream(mutablePropertySources.spliterator(), false)
                    .filter(propertySources -> propertySources instanceof EnumerablePropertySource)
                    .map(propertySources -> ((EnumerablePropertySource) propertySources).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .distinct()
                    .filter(propertyName -> !(
                            //빈감정보는 제외 처리
                            propertyName.contains("java.class.path") ||
                                    propertyName.contains("credentials") ||
                                    propertyName.contains("password") ||
                                    propertyName.contains("token") ||
                                    propertyName.contains("secret")))
                    .forEach(propertyName -> log.info("{}: {}", propertyName, environment.getProperty(propertyName)));
        }
         */

        //do more what you want..
    }

    @EventListener
    // occur when the app context close
    public void handleContextClosed(ContextClosedEvent event) {
        log.debug("call ContextClosedEvent : bye bye");
        //do more what you want..
    }

    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent hse) {
                log.debug("new session Created : {}", hse.getSession().getId());
                //do more what you want.
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent hse) {
                log.debug("session Destroyed : {}", hse.getSession().getId());
                //do more what you want.
            }
        };
    }

    /*
    @EventListener
    // 사용자 정의 이벤트 처리
    //todo : 어디에 활용하면 좋을까?
    public void handleCustomEvent(MyCustomEvent event) {
        log.debug("catched CustomEvent : " + event.getMessage());
    }
     */

}
