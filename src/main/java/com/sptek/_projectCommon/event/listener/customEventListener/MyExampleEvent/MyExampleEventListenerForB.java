package com.sptek._projectCommon.event.listener.customEventListener.MyExampleEvent;

import com.sptek._projectCommon.event.event.MyExampleEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyExampleEventListenerForB {

    @EventListener
    public void listen(MyExampleEvent myExampleEvent) {
        log.debug("Event! : {}", myExampleEvent.toString());
    }
}
