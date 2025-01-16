package com.sptek.webfw.eventListener.custom.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@ToString
@SuperBuilder
public class CustomEventBase {
    
    private final String eventMessage;

    @Builder.Default //기본값으로 지정됌
    private final long eventId = System.currentTimeMillis();

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();
}