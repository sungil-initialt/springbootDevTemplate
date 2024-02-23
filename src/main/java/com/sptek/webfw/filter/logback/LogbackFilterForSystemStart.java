package com.sptek.webfw.filter.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


public class LogbackFilterForSystemStart extends Filter<ILoggingEvent>{
    String justTag = "LBF>>"; //LogBackFilter
    String filterKeyword = justTag + "systemStart"; //example keyword.
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().contains(filterKeyword)) {
            return FilterReply.ACCEPT;
        }else{
            return FilterReply.DENY;
        }
    }
}
