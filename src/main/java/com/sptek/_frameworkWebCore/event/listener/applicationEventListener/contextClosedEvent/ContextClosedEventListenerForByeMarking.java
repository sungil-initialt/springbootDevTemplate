package com.sptek._frameworkWebCore.event.listener.applicationEventListener.contextClosedEvent;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextClosedEventListenerForByeMarking {

    @EventListener
    public void listen(ContextClosedEvent contextClosedEvent) {
        log.info(LoggingUtil.makeBaseForm(CommonConstants.FW_START_LOG_TAG, "Context Closed Event", "Bye~ Bye~ Application has been shut down successfully."));
        //do more what you want..
    }
}
