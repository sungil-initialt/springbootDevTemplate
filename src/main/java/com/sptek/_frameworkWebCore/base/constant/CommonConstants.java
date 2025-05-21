package com.sptek._frameworkWebCore.base.constant;

public class CommonConstants {

    // Logging
    public static final String REQ_PROPERTY_FOR_LOGGING_TIMESTAMP = "REQ_PROPERTY_FOR_LOGGING_TIMESTAMP";
    public static final String REQ_PROPERTY_FOR_LOGGING_MODELANDVIEW = "REQ_PROPERTY_FOR_LOGGING_MODELANDVIEW";
    public static final String REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE = "REQ_PROPERTY_FOR_LOGGING_EXCEPTION_MESSAGE";

    public static final String DEBUGGING_HELP_MESSAGE = "이 메시지가 로깅된 경우 sptfw 담당자에게 알려 주세요!";
    public static final String SERVER_INITIALIZATION_MARK = "FRAMEWORK INITIALIZATION NOTICE : ";

    public static final String LOCALE_NAME = "locale";
    public static final String TIMEZONE_NAME = "timezone";
    public static final int LOCALE_COOKIE_MAX_AGE_SEC = 60*60*24*7; //사용자 가 설정한 로케일 정보 쿠키를 얼마 동안 보존할 것인지

    public static final String UV_CHECK_LOG_NEW_VISITOR = "UV_CHECK_LOG_NEW_VISITOR";
}
