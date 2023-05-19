package com.sptek.webfw.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
@Slf4j
public class TypeConvertUtil {


    public static String mapToString(Map<String, String> map, String delimiter) {
        if (map == null) {
            return "";
        }

        delimiter = (delimiter == null) ? "\n" : delimiter;

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuilder.append(key).append("=").append(value).append(delimiter);
        }
        return stringBuilder.toString();
    }

    public static String mapToString2(Map<String, String[]> map, String delimiter) {
        if (map == null) {
            return "";
        }

        delimiter = (delimiter == null) ? "\n" : delimiter;

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            stringBuilder.append(key).append("=");

            if (values != null && values.length > 0) {
                stringBuilder.append(String.join(",", values));
            }

            stringBuilder.append(delimiter);
        }
        return stringBuilder.toString();
    }

}
