package com.sptek._frameworkWebCore.eventListener.publisher;

import com.sptek._frameworkWebCore.eventListener.custom.event.CustomEventBase;
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
