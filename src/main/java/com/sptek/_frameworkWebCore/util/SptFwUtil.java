package com.sptek._frameworkWebCore.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Slf4j
public class SptFwUtil {

    public static String convertSystemNotice(String title, StringBuffer logBody) {
        return SptFwUtil.convertSystemNotice(title, String.valueOf(logBody));
    }

    public static String convertSystemNotice(String title, String logBody) {
        return convertSystemNotice("", title, logBody);
    }

    public static String convertSystemNotice(String tagName, String title, String logBody) {
        return String.format(
                "%s\n"
                        + "--------------------\n"
                        + "SPT-FW [ **** %s **** ]\n"
                        + "--------------------\n"
                        + "%s\n"
                        + "--------------------\n"
                , tagName
                , title
                , Optional.ofNullable(logBody).map(SptFwUtil::removeLastNewline).orElse("")
        );
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
        // todo: CharacterEncoding 으로 판단 하는게 조금 무리가 있긴함
        if ("ISO-8859-1".equals(responseWrapper.getCharacterEncoding())) {
            return "----> Logging is skipped as the request body is a binary data.";
        }
        byte[] content = responseWrapper.getContentAsByteArray();
        if (content.length == 0) {
            return "No Content";
        }
        try {
            return new String(content, responseWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }

}
