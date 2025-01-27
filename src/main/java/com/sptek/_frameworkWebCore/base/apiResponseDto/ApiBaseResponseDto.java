package com.sptek._frameworkWebCore.base.apiResponseDto;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import com.sptek._frameworkWebCore.util.SpringUtil;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class ApiBaseResponseDto {
    public String resultCode;
    public String resultMessage;
    public String requestTime;
    public String responseTime;
    public String durationMsec;

    public void makeTimestamp() {
        this.requestTime = Optional.ofNullable(SpringUtil.getRequest().getAttribute(CommonConstants.REQ_PROPERTY_FOR_LOGGING_TIMESTAMP)).map(Object::toString).orElse("");
        this.responseTime = LocalDateTime.now().toString();
        this.durationMsec = StringUtils.hasText(requestTime) ? String.valueOf(Duration.between(LocalDateTime.parse(requestTime), LocalDateTime.parse(responseTime)).toMillis()) : "";
    }
}
