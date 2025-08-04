package com.sptek._frameworkWebCore.logging.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.util.FileSize;
import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KeywordBaseFileAppender extends AppenderBase<ILoggingEvent> {

    private final Map<String, RollingFileAppender<ILoggingEvent>> appenderCache = new ConcurrentHashMap<>();
    private LoggerContext context;

    // xml 설정값이 없을 경우의 디폹트 값
    @Setter private String baseLogPath = Path.of(".","log", "logback").toString();
    @Setter private String pattern = "%-5level %d{yy-MM-dd HH:mm:ss.SSS} [%thread] [%logger{0}.%method:%line] [MDC: %X{sessionId}, %X{memberId}] - %msg%n";
    @Setter private String fileMaxSize = "10MB";
    @Setter private int maxHistory = 1;

    @Override
    public void start() {
        this.context = (LoggerContext) getContext();
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        String message = event.getFormattedMessage();

        if (!message.startsWith(CommonConstants.FW_LOG_PREFIX)) return;

        String keyword = extractRealKeyword(message);
        if (keyword.isEmpty()) return;

        RollingFileAppender<ILoggingEvent> appender =
                appenderCache.computeIfAbsent(keyword, this::createAppender);
        appender.doAppend(event);
    }

    private String extractRealKeyword(String message) {
        int start = message.indexOf(CommonConstants.FW_LOG_PREFIX);
        if (start == -1) return "";
        start += CommonConstants.FW_LOG_PREFIX.length();

        int end = message.indexOf("\n", start);
        if (end == -1) end = message.length();

        String keyword = message.substring(start, end).trim();
        return keyword.startsWith(CommonConstants.FW_LOG_NO_CONSOLE_MARK) ? keyword.replaceFirst(CommonConstants.FW_LOG_NO_CONSOLE_MARK, "").trim() : keyword;
    }

    private RollingFileAppender<ILoggingEvent> createAppender(String keyword) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);

        try {
            Path folderPath = Path.of(baseLogPath, "CUSTOM", keyword);
            Files.createDirectories(folderPath);

            String logFile = folderPath.resolve(keyword + ".log").toString();
            appender.setFile(logFile);

            // Encoder
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern(pattern);
            encoder.start();
            appender.setEncoder(encoder);

            // Rolling Policy (TimeBased + Size)
            TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
            rollingPolicy.setContext(context);
            rollingPolicy.setParent(appender);
            rollingPolicy.setFileNamePattern(
                    folderPath.resolve(keyword + ".%d{yyyy-MM-dd}_%i.log").toString()
            );

            SizeAndTimeBasedFNATP<ILoggingEvent> triggeringPolicy = new SizeAndTimeBasedFNATP<>();
            triggeringPolicy.setContext(context);
            triggeringPolicy.setMaxFileSize(FileSize.valueOf(fileMaxSize));
            rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(triggeringPolicy);

            rollingPolicy.setMaxHistory(maxHistory);
            rollingPolicy.start();

            appender.setRollingPolicy(rollingPolicy);
            appender.start();

        } catch (IOException e) {
            addError("Failed to create log appender for keyword [" + keyword + "]", e);
        }

        return appender;
    }
}
