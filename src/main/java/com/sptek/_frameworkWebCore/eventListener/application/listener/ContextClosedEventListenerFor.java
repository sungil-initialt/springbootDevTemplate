package com.sptek._frameworkWebCore.eventListener.application.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextClosedEventListenerFor {

    @EventListener
    public void contextClosedEvent(ContextClosedEvent contextClosedEvent) {
        log.debug("caught ContextClosedEvent : bye bye");
        //do more what you want..
    }
}
