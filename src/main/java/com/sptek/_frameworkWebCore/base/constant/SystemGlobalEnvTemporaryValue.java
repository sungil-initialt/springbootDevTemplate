package com.sptek._frameworkWebCore.base.constant;

import com.sptek._frameworkWebCore._annotation.Enable_GlobalEnvLog_At_Main;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.StreamSupport;

@Slf4j

public class SystemGlobalEnvTemporaryValue {
    //단순 로깅만 처리함 (내부 변수만 사용하여 민감 정보에 대한 GC 가 바로 이루어 지도록 처리) // todo: 보안 이슈는 없을까?
    public SystemGlobalEnvTemporaryValue(ApplicationContext applicationContext) {
        // 민감 정보를 포함하기 때문에 @Enable_GlobalEnvLogging_At_Main 가 존재하는 경우만 동작하게 처리함
        if (MainClassAnnotationRegister.hasAnnotation(Enable_GlobalEnvLog_At_Main.class)) {
            Environment environment = applicationContext.getEnvironment();
            String[] activeProfiles = environment.getActiveProfiles();

            StringBuffer globalEnvironment = new StringBuffer((String.format("activeProfiles : %s\n", Arrays.toString(activeProfiles))));

            final MutablePropertySources mutablePropertySources = ((AbstractEnvironment) environment).getPropertySources();
            StreamSupport.stream(mutablePropertySources.spliterator(), false)
                    .filter(propertySources -> propertySources instanceof EnumerablePropertySource)
                    .map(propertySources -> ((EnumerablePropertySource<?>) propertySources).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .distinct()
                    //                .filter(propertyName -> {
                    //                    // 대소문자 구분 없이 키에 특정 단어가 포함된 경우 필터링
                    //                    String pattern = "(?i).*(" + String.join("|", getExceptKeyword()) + ").*";  // 대소문자 구분 없는 패턴
                    //                    Pattern p = Pattern.compile(pattern);
                    //                    Matcher m = p.matcher(propertyName);
                    //                    return !m.matches();  // "credentials", "password", "token", "secret"이 포함된 경우 필터링
                    //                })
                    .forEach(propertyName -> {
                        globalEnvironment.append(String.format("%s : %s\n", propertyName, environment.getProperty(propertyName)));
                    });

            log.info(LoggingUtil.makeFwLogForm("System Global Env (Notice!! : May Contain Confidential Details)", globalEnvironment.toString(), CommonConstants.GLOBAL_ENV_LOG_MARK));
        }
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
