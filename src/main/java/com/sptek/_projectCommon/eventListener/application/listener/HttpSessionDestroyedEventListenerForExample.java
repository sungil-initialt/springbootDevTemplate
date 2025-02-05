package com.sptek._projectCommon.eventListener.application.listener;

import com.sptek._frameworkWebCore.eventListener.application.listener.HttpSessionListenerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Configuration
public class HttpSessionDestroyedEventListenerForExample {

    @EventListener
    public void httpSessionDestroyedEventAdapter(HttpSessionListenerAdapter.HttpSessionDestroyedEventAdapter httpSessionDestroyedEventAdapter) {
        log.debug("caught HttpSessionDestroyedEvent : sessionId({})", httpSessionDestroyedEventAdapter.httpSessionEvent.getSession().getId());
        //do more what you want..
    }
}