package com.sptek._frameworkWebCore.event.listener.applicationEventListener.contextRefreshedEvent;

import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.SystemGlobalEnvTemporaryValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Profile(value = { "local", "dev", "stg", "prd" }) //prd는 제외 할까?(보안이슈)
//@ConditionalOnProperty(name = "sptFramework.eventListener.application.ContextRefreshedEventListenerForConfigLogging", havingValue = "true", matchIfMissing = false) //@HasAnnotationOnMain_InBean 방식으로 변경
@Component
public class ContextRefreshedEventListenerForFwResourceLoading {

    // 애플리케이션 컨텍스트가 초기화되거나 새로고침될 때 실행 (시스템 설정상의 문제를 확인하는데 도움을 줄 수 있다)
    @EventListener
    public void listen(ContextRefreshedEvent contextRefreshedEvent) throws Exception {
        log.debug("Event!");
        new MainClassAnnotationRegister(contextRefreshedEvent.getApplicationContext());
        new RequestMappingAnnotationRegister(contextRefreshedEvent.getApplicationContext());
        new SystemGlobalEnvTemporaryValue(contextRefreshedEvent.getApplicationContext()); // todo: MainClassAnnotationRegister 보단 항상 뒤에 생성되야 함 (제약이 없도록 수정하면 좋을듯)
    }
}

//--> noConsole 처리, SystemGlobalEnvTemporaryRegister 가 noConsole 되도록 처리