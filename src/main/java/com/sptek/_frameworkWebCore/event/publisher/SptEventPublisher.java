package com.sptek._frameworkWebCore.event.publisher;

import com.sptek._frameworkWebCore.event.event.SptBaseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SptEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    // CustomEventBase 상속형만 가능하도록 처리
    public void publishEvent(SptBaseEvent SptBaseEvent) {
        applicationEventPublisher.publishEvent(SptBaseEvent);
    }
}
