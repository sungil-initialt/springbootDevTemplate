package com.sptek._frameworkWebCore._example.unit.event;

import com.sptek._frameworkWebCore.event.event.SptBaseEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class MyEvent extends SptBaseEvent {
    private String extraField;
}
