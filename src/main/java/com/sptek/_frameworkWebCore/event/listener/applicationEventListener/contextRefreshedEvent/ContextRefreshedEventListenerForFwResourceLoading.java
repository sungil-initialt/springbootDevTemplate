package com.sptek._frameworkWebCore.event.listener.applicationEventListener.contextRefreshedEvent;

import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.RequestMappingAnnotationRegister;
import com.sptek._frameworkWebCore.base.constant.SystemGlobalEnvTemporaryValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ContextRefreshedEventListenerForFwResourceLoading {
    @EventListener
    public void listen(ContextRefreshedEvent contextRefreshedEvent) throws Exception {
        log.debug("Event!");
        new MainClassAnnotationRegister(contextRefreshedEvent.getApplicationContext());
        new RequestMappingAnnotationRegister(contextRefreshedEvent.getApplicationContext());
        new SystemGlobalEnvTemporaryValue(contextRefreshedEvent.getApplicationContext()); // todo: MainClassAnnotationRegister 보단 항상 뒤에 생성되야 함 (제약이 없도록 수정하면 좋을듯)
    }
}