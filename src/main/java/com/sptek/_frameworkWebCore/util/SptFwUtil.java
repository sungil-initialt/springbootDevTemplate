package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.constant.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.UnsupportedEncodingException;

@Slf4j
public class SptFwUtil {

    public static String convertSystemNotice(@Nullable String title, @Nullable StringBuffer logContent) { return SptFwUtil.convertSystemNotice(title, String.valueOf(logContent));}
    public static String convertSystemNotice(@Nullable String title, @Nullable String logContent) { return convertSystemNotice(null, title, logContent); }
    public static String convertSystemNotice(@Nullable String tagName, @Nullable String title, @Nullable String logContent) {
        //tagName 은 해당 로깅의 시작 키워드로 지정되며 로그 내용을 검색하기 위한 키워드 또는 파일로 저장하기 위한 기준으로 활용
        tagName = StringUtils.hasText(tagName) && !tagName.equals("null") ? tagName : "";
        title = StringUtils.hasText(title) && !title.equals("null") ? title : "No Title";
        logContent = StringUtils.hasText(logContent) && !logContent.equals("null") ? logContent : "No Log Body";

        return """
            %s %s
            --------------------
            SPT-FW [ **** %s **** ]
            --------------------
            %s
            --------------------
            """
            .formatted(CommonConstants.SYSTEM_NOTICE_TAG, tagName, title, SptFwUtil.removeLastNewline(logContent));
    }

    public static String removeLastNewline(String string) {
        if (string != null && string.endsWith("\n")) {
            return string.substring(0, string.length() - 1);
        }
        return string;
    }

    public static String getRequestBody(ContentCachingRequestWrapper requestWrapper) {
        byte[] content = requestWrapper.getContentAsByteArray();
        if (content.length == 0) {
            return "";
        }
        try {
            return new String(content, requestWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }

    public static String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        byte[] content = responseWrapper.getContentAsByteArray();
        if (content.length == 0) {
            return "No Content";
        }

        if (content.length > 3000_0) { // 30k
            return "-> The body is too big and skipped";
        }

        try {
            return new String(content, responseWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }

}
