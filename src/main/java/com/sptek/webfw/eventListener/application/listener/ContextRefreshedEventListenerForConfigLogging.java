package com.sptek.webfw.eventListener.application.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Slf4j
@Component
@Profile(value = { "local", "dev", "stg" })
public class ContextRefreshedEventListenerForConfigLogging {

    //Application 관련 Listener의 경우 bean 의 형태로 등록도 가능함(아래 방식이 확정성이 더 좋아 보임)
    //@Bean
    //public ApplicationListener applicationListener() {
    //    return new ApplicationListener() {
    //        @Override
    //        public void onApplicationEvent(ApplicationEvent event) {
    //        }
    //    };
    //}

    // 애플리케이션 컨텍스트가 초기화되거나 새로고침될 때 실행
    @EventListener
    public void contextRefreshedEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.debug("catched ContextRefreshedEvent : Hi~");

        Environment environment = contextRefreshedEvent.getApplicationContext().getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();

        //시스템 설정 프로퍼티 모두 노출(시스템 설정상의 문제를 확인하는데 도움을 줄 수 있다)
        if(Boolean.parseBoolean(environment.getProperty("system.propertySources.expose-major-enabled"))) {
            StringBuffer logString = new StringBuffer("\n--------------------\n[ ContextRefresh : Major Environment Information ]\n");
            logString.append(String.format("activeProfiles : %s\n", Arrays.toString(activeProfiles)));

            final MutablePropertySources mutablePropertySources = ((AbstractEnvironment) environment).getPropertySources();
            StreamSupport.stream(mutablePropertySources.spliterator(), false)
                    .filter(propertySources -> propertySources instanceof EnumerablePropertySource)
                    .map(propertySources -> ((EnumerablePropertySource) propertySources).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .distinct()
                    .filter(propertyName -> {
                        // 대소문자 구분 없이 키에 특정 단어가 포함된 경우 필터링
                        String pattern = "(?i).*(" + String.join("|", getExceptKeyword()) + ").*";  // 대소문자 구분 없는 패턴
                        Pattern p = Pattern.compile(pattern);
                        Matcher m = p.matcher(propertyName);
                        return !m.matches();  // "credentials", "password", "token", "secret"이 포함된 경우 필터링
                    })
                    .forEach(propertyName -> {
                        logString.append(String.format("%s : %s\n", propertyName, environment.getProperty(propertyName)));
                    });
            logString.append("--------------------\n");
            log.info(logString.toString());
        }

        //if(Boolean.parseBoolean(environment.getProperty("system.propertySources.expose-sumarry-enabled"))) {
        //
        //}
    }

    //예외 키워드 설정
    private Set<String> getExceptKeyword() {
        return Set.of(
                "line.separator"
                , "CommonProgramFiles"
                , "path"
                , "credentials"
                , "password"
                , "token"
                , "secret"
                , "CHARSET"
                , "encoding"
                , "home"
                , "program"
                , "java"
                , "dir"
        );
    }
}
