package com.sptek._frameworkWebCore.logging.appender;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
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
    @Setter private String encoderPattern = "%d{yy-MM-dd HH:mm:ss.SSS} [MDC: %X{sessionId}, %X{memberId}] - %msg%n"; // 성능 고려 간략화
    @Setter private String baseLogPath = Path.of(".","log", "logback").toString();
    @Setter private String rollingFilePattern = ".%d{yyyy-MM-dd}_%i.log";
    @Setter private String fileMaxSize = "100MB";
    @Setter private int maxHistory = 31;
    @Setter private String totalSizeCap = "10GB";

    @Override
    public void start() {
        this.context = (LoggerContext) getContext();
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        String msg = event.getFormattedMessage();
        if (!msg.startsWith(CommonConstants.FW_LOG_PREFIX)) return;

        int newlineIndex = msg.indexOf('\n');
        String firstLine = newlineIndex >= 0 ? msg.substring(0, newlineIndex) : msg;
        String appenderKeyword = extractFileName(firstLine);

        //ystem.out.println("extractFileName : " + fileName);
        if (appenderKeyword.isEmpty()) return;
        RollingFileAppender<ILoggingEvent> appender = appenderCache.computeIfAbsent(appenderKeyword, this::createAppender);
        appender.doAppend(event);
    }

    // fileName 을 키워드로 사용하는 케이스
    public static String extractFileName(String text) {
        int i = text.indexOf(CommonConstants.FW_LOG_FILENAME_MARK);
        if (i < 0) return "";

        int start = i + CommonConstants.FW_LOG_FILENAME_MARK.length();
        int len = text.length();

        // fileName 의 끝 위치(첫 공백 또는 문자열 끝)
        int end = start;
        while (end < len && text.charAt(end) != ' ') {
            end++;
        }
        return text.substring(start, end);
    }

    private RollingFileAppender<ILoggingEvent> createAppender(String keyword) {
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setContext(context);

        try {
            // Encoder
            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(context);
            encoder.setPattern(encoderPattern);
            encoder.start();
            appender.setEncoder(encoder);

            // log file
            Path folderPath = Path.of(baseLogPath, CommonConstants.FW_LOG_BASE_DIR, keyword);
            Files.createDirectories(folderPath);
            String logFile = folderPath.resolve(keyword + ".log").toString();
            appender.setFile(logFile);

            // Rolling Policy
            SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
            rollingPolicy.setContext(context);
            rollingPolicy.setParent(appender);
            rollingPolicy.setFileNamePattern(folderPath.resolve(keyword + rollingFilePattern).toString());
            rollingPolicy.setMaxFileSize(FileSize.valueOf(fileMaxSize)); // 예: "10MB"
            rollingPolicy.setMaxHistory(maxHistory);
            rollingPolicy.setTotalSizeCap(FileSize.valueOf(totalSizeCap));
            rollingPolicy.start();

            appender.setRollingPolicy(rollingPolicy);
            appender.setTriggeringPolicy(rollingPolicy);
            appender.start();

        } catch (IOException e) {
            addError("Failed to create log appender for keyword [" + keyword + "]", e);
        }
        return appender;
    }
}
