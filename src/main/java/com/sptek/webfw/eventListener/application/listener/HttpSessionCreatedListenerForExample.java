package com.sptek.webfw.eventListener.application.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpSessionCreatedListenerForExample {

    // todo: HttpSessionCreatedEvent 는 springSecutity 설정에 따라 제한됨으로 범용적 사용이 조금 어려울 듯 하여 보류
    //@EventListener
    //public void httpSessionCreatedEvent(HttpSessionCreatedEvent httpSessionCreatedEvent) {
    //    log.debug("catched HttpSessionCreatedEvent : sessionId({})", httpSessionCreatedEvent.getSession().getId());
    //    //do more what you want..
    //}

    @EventListener
    public void httpSessionCreatedEventAdapter(HttpSessionListenerAdapter.HttpSessionCreatedEventAdapter HttpSessionCreatedEventAdapter) {
        log.debug("catched HttpSessionCreatedEventAdapter : sessionId({})", HttpSessionCreatedEventAdapter.httpSessionEvent.getSession().getId());
        //do more what you want..
    }
}
