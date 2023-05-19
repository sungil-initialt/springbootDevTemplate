package com.sptek.webfw.util;

import org.apache.commons.lang3.StringEscapeUtils;

public class SecureUtil {

    public static String charEscape(String orgStr) {
        return orgStr == null ? orgStr : StringEscapeUtils.escapeHtml4(orgStr);
    }



}
