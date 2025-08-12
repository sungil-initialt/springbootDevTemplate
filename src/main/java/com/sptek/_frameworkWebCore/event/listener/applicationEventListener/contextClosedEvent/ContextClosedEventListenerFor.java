package com.sptek._frameworkWebCore.event.listener.applicationEventListener.contextClosedEvent;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ContextClosedEventListenerFor {

    @EventListener
    public void listen(ContextClosedEvent contextClosedEvent) {
        log.info(LoggingUtil.makeFwLogForm("Context Closed Event"
                , "Bye~ Bye~ System has been shut down successfully.", CommonConstants.FW_START_LOG_TAG));
        //log.debug("Event! : bye bye");
        //do more what you want..
    }
}
