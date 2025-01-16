package com.sptek.webfw.eventListener.custom.event;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class ExampleEvent extends CustomEventBase{
    private String extraField;
}
