package com.sptek.serviceName._global.eventListener.application.listener;

import com.sptek._frameworkWebCore.eventListener.application.listener.HttpSessionListenerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpSessionCreatedEventListenerForExample {

    @EventListener
    public void httpSessionCreatedEventAdapter(HttpSessionListenerAdapter.HttpSessionCreatedEventAdapter HttpSessionCreatedEventAdapter) {
        log.debug("caught HttpSessionCreatedEventAdapter : sessionId({})", HttpSessionCreatedEventAdapter.httpSessionEvent.getSession().getId());
        //do more what you want..
    }
}
