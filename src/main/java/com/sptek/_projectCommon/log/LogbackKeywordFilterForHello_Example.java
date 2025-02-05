package com.sptek._projectCommon.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


public class LogbackKeywordFilterForHello_Example extends Filter<ILoggingEvent>{
    String filterKeyword = "Hello"; //LogBackFilter keywork example.

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().startsWith(filterKeyword)) {
            //조건에 맞을때 log 처리함
            return FilterReply.ACCEPT;
        }else{
            return FilterReply.DENY;
        }
    }
}
