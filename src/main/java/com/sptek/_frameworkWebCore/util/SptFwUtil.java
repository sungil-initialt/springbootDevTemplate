package com.sptek._frameworkWebCore.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Slf4j
public class SptFwUtil {

    public static String convertSystemNotice(String title, StringBuffer logBody) { return SptFwUtil.convertSystemNotice(title, String.valueOf(logBody));}
    public static String convertSystemNotice(String title, String logBody) { return convertSystemNotice("", title, logBody); }
    public static String convertSystemNotice(String tagName, String title, String logBody) {
        //tagName 은 해당 로깅의 시작 키워드로 지정되며 로그 내용을 검색하기 위한 키워드 또는 파일로 저장하기 위한 기준으로 활용
        return String.format(
                "↓ Tag Name : %s\n"
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
