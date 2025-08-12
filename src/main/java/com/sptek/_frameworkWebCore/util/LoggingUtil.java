package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingUtil {

    public static String makeFwLogForm(String title, String content) {
        return makeFwLogForm(title, content, "");
    }

    public static String makeFwLogForm(String title, String content, String logTag) {
        // 변경시 주의(아래 형태가 다른 코드에 영향이 있음)
        return """
            %s%s
            --------------------------------------------------------------------------------
            [ **** %s **** ]
            --------------------------------------------------------------------------------
            %s
            --------------------------------------------------------------------------------
            """
            .formatted(CommonConstants.FW_LOG_PREFIX, logTag, title, LoggingUtil.removeLastNewline(content));
    }

    public static String makeFwLogSimpleForm(String content, String logTag) {
        // 변경시 주의(아래 형태가 다른 코드에 영향이 있음)
        return "%s%s => %s".formatted(CommonConstants.FW_LOG_PREFIX, logTag, LoggingUtil.removeLastNewline(content));
    }

    public static String removeLastNewline(String string) {
        if (string != null && string.endsWith("\n")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }
}
