package com.sptek.webfw.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class SptFwUtil {
    public static String convertSystemNotice(String title, StringBuffer logBody) {
        return SptFwUtil.convertSystemNotice(title, String.valueOf(logBody));
    }

    public static String convertSystemNotice(String title, String logBody) {
        return String.format(
                  "\n\n"
                + "--------------------\n"
                + "[ **** %s **** ]\n"
                + "--------------------\n"
                + "%s\n"
                + "--------------------\n"
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

}
