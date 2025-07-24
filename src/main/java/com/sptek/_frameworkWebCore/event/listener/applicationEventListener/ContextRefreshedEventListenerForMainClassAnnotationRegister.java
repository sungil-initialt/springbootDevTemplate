package com.sptek._frameworkWebCore.event.listener.applicationEventListener;

import com.sptek._frameworkWebCore.base.constant.MainClassAnnotationRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextRefreshedEventListenerForMainClassAnnotationRegister {
    @EventListener
    public void listen(ContextRefreshedEvent contextRefreshedEvent) throws Exception {
        log.debug("Event!");
        //시스템 로딩이 완료된 시점에 MainClassAnnotationRegister 를 생성해 준다.
        new MainClassAnnotationRegister(contextRefreshedEvent.getApplicationContext());

        //log.debug("ContextRefreshedEventListenerForMainClassAnnotationRegister : {}, {}", MainClassAnnotationRegister.hasAnnotation(EnableUvCheckLog_InMainl.class), MainClassAnnotationRegister.hasAnnotation(TestAnnotation_InAll.class));
        //log.debug("ContextRefreshedEventListenerForMainClassAnnotationRegister : {}, {}", MainClassAnnotationRegister.getAnnotationAttributes(EnableUvCheckLog_InMainl.class).get("value"), MainClassAnnotationRegister.getAnnotationAttributes(TestAnnotation_InAll.class).get("value"));
   }
}