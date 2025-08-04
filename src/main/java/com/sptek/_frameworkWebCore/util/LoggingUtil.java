package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

@Slf4j
public class LoggingUtil {

    public static String makeFwLogForm(@Nullable String title, @Nullable String content) {
        return makeFwLogForm(title, content, null);
    }

    public static String makeFwLogForm(@Nullable String title, @Nullable String content, @Nullable String logFileName) {
        title = StringUtils.hasText(title) && !title.equals("null") ? title : "No Title";
        content = StringUtils.hasText(content) && !content.equals("null") ? content : "No Content";
        logFileName = StringUtils.hasText(logFileName) && !logFileName.equals("null") ? logFileName : "";

        // 아래 형태가 다른 코드에 영향을 줌으로 변경시 주의
        return """
            %s%s
            --------------------------------------------------------------------------------
             [ **** %s **** ]
            --------------------------------------------------------------------------------
            %s
            --------------------------------------------------------------------------------
            """
            .formatted(CommonConstants.FW_LOG_PREFIX, logFileName, title, LoggingUtil.removeLastNewline(content));
    }

    public static String makeFwLogSimpleForm(@Nullable String content, @Nullable String logFileName) {
        content = StringUtils.hasText(content) && !content.equals("null") ? content : "No Content";
        logFileName = StringUtils.hasText(logFileName) && !logFileName.equals("null") ? logFileName : "";

        // 아래 형태가 다른 코드에 영향을 줌으로 변경시 주의
        return """
            %s%s
            %s
            """
            .formatted(CommonConstants.FW_LOG_PREFIX, logFileName, LoggingUtil.removeLastNewline(content));
    }

    public static String removeLastNewline(String string) {
        if (string != null && string.endsWith("\n")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }
}
