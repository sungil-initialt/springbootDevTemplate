package com.sptek._frameworkWebCore.event.listener.applicationEventListener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextClosedEventListenerFor {

    @EventListener
    public void listen(ContextClosedEvent contextClosedEvent) {
        log.debug("Event! : bye bye");
        //do more what you want..
    }
}
