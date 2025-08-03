package com.sptek._frameworkWebCore.base.constant;

import org.springframework.stereotype.Component;

@Component
public class CommonConstants {
    // project
    public static final String PROJECT_PACKAGE_NAME = "com.sptek.";
    public static final String FRAMEWORK_WEBCORE_PACKAGE_NAME = PROJECT_PACKAGE_NAME + "_frameworkWebCore.";
    public static final String FRAMEWORK_ANNOTATION_PACKAGE_NAME = FRAMEWORK_WEBCORE_PACKAGE_NAME + "annotation";

    // Logging
    public static final String FW_LOG_PREFIX = "↓ FW_LOG_TAG : ";
    public static final String FW_LOG_NO_CONSOLE_MARK = "noConsole->";
    public static final String GLOBAL_ENV_LOG_MARK = FW_LOG_NO_CONSOLE_MARK + "SYSTEM_GLOBAL_ENV";
    public static final String REQ_PROPERTY_FOR_LOGGING_TIMESTAMP = "REQ_PROPERTY_FOR_LOGGING_TIMESTAMP";
    public static final String REQ_PROPERTY_FOR_LOGGING_MODELANDVIEW = "REQ_PROPERTY_FOR_LOGGING_MODELANDVIEW";
    public static final String REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE = "REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE";
    public static final String REQ_PROPERTY_FOR_LOGGING_RELATED_OUTBOUNDS = "REQ_PROPERTY_FOR_LOGGING_RELATED_OUTBOUNDS";

    public static final String DEBUGGING_HELP_MESSAGE = "이 메시지가 보인다면!! FW Core 담당자 에게 알려 주세요!";
    public static final String SERVER_INITIALIZATION_MARK = "FRAMEWORK INITIALIZATION NOTICE : ";

    // locale
    public static final String LOCALE_NAME = "locale";
    public static final String TIMEZONE_NAME = "timezone";
    public static final int LOCALE_COOKIE_MAX_AGE_SEC = 60*60*24*7; //사용자 가 설정한 로케일 정보 쿠키를 얼마 동안 보존할 것인지

    // uv filter
    public static final String UV_CHECK_LOG_NEW_VISITOR = "UV_CHECK_LOG_NEW_VISITOR";

    // spring-security
    public static final String ANONYMOUS_USER = "anonymousUser";
}
