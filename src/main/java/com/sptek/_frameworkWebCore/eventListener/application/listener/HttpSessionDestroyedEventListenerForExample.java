package com.sptek._frameworkWebCore.eventListener.application.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Configuration
public class HttpSessionDestroyedEventListenerForExample {

    // todo: HttpSessionDestroyedEvent 는 springSecutity 설정에 따라 제한됨으로 범용적 사용이 조금 어려울 듯 하여 보류
    //@EventListener
    //public void httpSessionDestroyedEvent(HttpSessionDestroyedEvent httpSessionDestroyedEvent) {
    //    log.debug("catched HttpSessionDestroyedEvent : sessionId({})", httpSessionDestroyedEvent.getSession().getId());
    //    //do more what you want..
    //}


    @EventListener
    public void httpSessionDestroyedEventAdapter(HttpSessionListenerAdapter.HttpSessionDestroyedEventAdapter httpSessionDestroyedEventAdapter) {
        log.debug("catched HttpSessionDestroyedEvent : sessionId({})", httpSessionDestroyedEventAdapter.httpSessionEvent.getSession().getId());
        //do more what you want..
    }
}
