package com.sptek._projectCommon.event.event;

import com.sptek._frameworkWebCore.event.event.SptBaseEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class MyCustomEvent1 extends SptBaseEvent {
    private String extraField;
}
