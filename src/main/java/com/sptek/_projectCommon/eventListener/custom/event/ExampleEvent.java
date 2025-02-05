package com.sptek._projectCommon.eventListener.custom.event;

import com.sptek._frameworkWebCore.eventListener.custom.event.CustomEventBase;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
public class ExampleEvent extends CustomEventBase {
    private String extraField;
}
