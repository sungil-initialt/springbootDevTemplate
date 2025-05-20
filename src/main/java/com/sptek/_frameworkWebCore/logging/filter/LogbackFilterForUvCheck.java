package com.sptek._frameworkWebCore.logging.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;


public class LogbackFilterForUvCheck extends Filter<ILoggingEvent>{

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().startsWith(CommonConstants.UV_CHECK_LOG_CREATE_NEW)) {
            //조건에 맞을때 log 처리함
            return FilterReply.ACCEPT;
        }else{
            return FilterReply.DENY;
        }
    }
}
