package com.sptek._projectCommon.event.listener.customEventListener;

import com.sptek._projectCommon.event.event.MyCustomEvent1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyCustomEvent1ListenerForA {

    @EventListener
    public void listen(MyCustomEvent1 exampleEvent) {
        log.debug("Event! : {}", exampleEvent.toString());
    }
}
