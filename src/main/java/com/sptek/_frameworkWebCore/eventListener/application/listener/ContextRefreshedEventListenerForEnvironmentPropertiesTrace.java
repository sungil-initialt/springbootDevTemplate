package com.sptek._frameworkWebCore.eventListener.application.listener;

import com.sptek._frameworkWebCore.annotation.EnableEnvironmentPropertiesTrace_InMain;
import com.sptek._frameworkWebCore.annotation.annotationCondition.HasAnnotationOnMain_InBean;
import com.sptek._frameworkWebCore.util.SptFwUtil;
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
@Profile(value = { "local", "dev", "stg", "prd" }) //prd는 제외 할까?(보안이슈)
//@ConditionalOnProperty(name = "sptFramework.eventListener.application.ContextRefreshedEventListenerForConfigLogging", havingValue = "true", matchIfMissing = false) //@HasAnnotationOnMain_InBean 방식으로 변경
@HasAnnotationOnMain_InBean(EnableEnvironmentPropertiesTrace_InMain.class)
@Component
public class ContextRefreshedEventListenerForEnvironmentPropertiesTrace {

    // 애플리케이션 컨텍스트가 초기화되거나 새로고침될 때 실행 (시스템 설정상의 문제를 확인하는데 도움을 줄 수 있다)
    @EventListener
    public void contextRefreshedEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Environment environment = contextRefreshedEvent.getApplicationContext().getEnvironment();
        String[] activeProfiles = environment.getActiveProfiles();

        StringBuffer logBody = new StringBuffer((String.format("activeProfiles : %s\n", Arrays.toString(activeProfiles))));

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
                    logBody.append(String.format("%s : %s\n", propertyName, environment.getProperty(propertyName)));
                });

        log.info(SptFwUtil.convertSystemNotice("Major Environment Information", logBody.toString()));
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
