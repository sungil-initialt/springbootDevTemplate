package com.sptek.webfw.eventListener.publisher;

import com.sptek.webfw.eventListener.custom.event.CustomEventBase;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    // CustomEventBase 상속형만 가능하도록 처리
    public void publishEvent(CustomEventBase CustomEventBase) {
        applicationEventPublisher.publishEvent(CustomEventBase);
    }
}
