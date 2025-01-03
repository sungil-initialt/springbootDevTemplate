package com.sptek.webfw.common.responseDto;

import com.sptek.webfw.util.SpringUtil;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class CommonApiResponse {
    public String resultCode;
    public String resultMessage;
    public String requestTime;
    public String responseTime;
    public String durationMsec;

    public void makeTimestamp() {
        this.requestTime = Optional.ofNullable(SpringUtil.getRequest().getAttribute(SpringUtil.getProperty("request.reserved.attribute.requestTimeStamp", "REQUEST_TIME_STAMP"))).map(Object::toString).orElse("");
        this.responseTime = LocalDateTime.now().toString();
        this.durationMsec = StringUtils.hasText(requestTime) ? String.valueOf(Duration.between(LocalDateTime.parse(requestTime), LocalDateTime.parse(responseTime)).toMillis()) : "";
    }
}
