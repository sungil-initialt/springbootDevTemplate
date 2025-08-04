package com.sptek._frameworkWebCore.logging.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;


public class LogbackFilterForExceptConsole extends Filter<ILoggingEvent>{

    @Override
    public FilterReply decide(ILoggingEvent event) {
        // todo: (중요) logback filter에서는 스프링 관련 코드, static 값, bean을 직접 참조하지 말 것! (별도의 classLoader 가 사용 되는 듯)
        // MainClassAnnotationRegister 등 static 클레스 사용 하지 말것 (서로 다른 공간에서 instance 화 되는 듯)
        if (
           (event.getMessage().startsWith(CommonConstants.FW_LOG_PREFIX + CommonConstants.FW_LOG_NO_CONSOLE_MARK)) ||
           (event.getMessage().startsWith(CommonConstants.VISIT_HISTORY_NEW_VISITOR_LOG) || event.getMessage().startsWith(CommonConstants.VISIT_HISTORY_EXIST_VISITOR_LOG))
        ) {
            return FilterReply.DENY;
        } else {
            return FilterReply.ACCEPT;
        }
    }
}
