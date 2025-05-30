package com.sptek._projectCommon.eventListener.custom.listener;

import com.sptek._projectCommon.eventListener.custom.event.ExampleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExampleEventListenerForA {

    @EventListener
    public void exampleEvent(ExampleEvent exampleEvent) {
        log.debug("caught ExampleEvent : eventId({}), eventAt({}), eventMessage({}), extraField({})"
                , exampleEvent.getEventId(), exampleEvent.getTimestamp(), exampleEvent.getEventMessage(), exampleEvent.getExtraField());
    }
}
