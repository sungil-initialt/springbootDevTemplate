package com.sptek._frameworkWebCore.base.apiResponseDto;

import com.sptek._frameworkWebCore.commonObject.dto.ExcuteTimeDto;
import com.sptek._frameworkWebCore.util.RequestUtil;

public class ApiBaseResponseDto {
    public String resultCode;
    public String resultMessage;
    public String requestTime;
    public String responseTime;
    public String durationMsec;

    public void makeTimestamp() {
        ExcuteTimeDto excuteTimeDto = RequestUtil.traceRequestDuration();
        this.requestTime = excuteTimeDto.getStartTime();
        this.responseTime = excuteTimeDto.getCurrentTime();
        this.durationMsec = excuteTimeDto.getDurationMsec();
    }
}
