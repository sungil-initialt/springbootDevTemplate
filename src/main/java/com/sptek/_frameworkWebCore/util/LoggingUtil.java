package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;

@Slf4j
public class LoggingUtil {
    public static String makeSimpleForm(String content) {
        return makeSimpleForm("", content);
    }

    public static String makeSimpleForm(String logTag, String content) {
        // 변경시 주의(아래 형태가 다른 코드에 영향이 있음)
        return "%s%s => %s".formatted(CommonConstants.FW_LOG_PREFIX, logTag, LoggingUtil.removeLastNewline(content));
    }

    public static String makeBaseForm(String title, String content) {
        return makeBaseForm("", title, content);
    }

    public static String makeBaseForm(String logTag, String title, String content) {
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

    public static String removeLastNewline(String string) {
        if (string != null && string.endsWith("\n")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }

    // Lazy 로깅을 처리하기 할때 사용 (성능 향상) ---------------------------------------------------------------------------------------------
    private static final String SIMPLE_FORM = "{}{} => ";
    private static final String BASE_FORM_HEADER = """
            {}{}
            --------------------------------------------------------------------------------
            [ **** {} **** ]
            --------------------------------------------------------------------------------
            """;
    private static final String BASE_FORM_BOTTOM = """
            --------------------------------------------------------------------------------
            """;

    // args에는 Supplier<?> 또는 값(Object)을 섞어서 넘겨도 됨 (lazy + eager 혼용)
    public static void logSimpleForm(Logger logger, Level level, String logTag, String bodyTemplate, Object... args) {
        if (!logger.isEnabledForLevel(level)) return;

        LoggingEventBuilder loggingEventBuilder = logger.atLevel(level).setMessage(SIMPLE_FORM + removeLastNewline(bodyTemplate))
                .addArgument(CommonConstants.FW_LOG_PREFIX)
                .addArgument(logTag);

        // 바디 인자들: Supplier면 lazy, 아니면 즉시 값으로 처리
        for (Object object : args) {
            if (object instanceof java.util.function.Supplier<?> supplier) loggingEventBuilder.addArgument(supplier);
            else loggingEventBuilder.addArgument(object);
        }
        loggingEventBuilder.log();
    }

    public static void logBaseForm(Logger logger, Level level, String logTag, String title, String bodyTemplate, Object... args) {
        if (!logger.isEnabledForLevel(level)) return;

        LoggingEventBuilder loggingEventBuilder = logger.atLevel(level).setMessage(BASE_FORM_HEADER + bodyTemplate + BASE_FORM_BOTTOM)
                .addArgument(CommonConstants.FW_LOG_PREFIX)
                .addArgument(logTag)
                .addArgument(title);

        // 바디 인자들: Supplier면 lazy, 아니면 즉시 값으로 처리
        for (Object object : args) {
            if (object instanceof java.util.function.Supplier<?> supplier) loggingEventBuilder.addArgument(supplier);
            else loggingEventBuilder.addArgument(object);
        }
        loggingEventBuilder.log();
    }
}
