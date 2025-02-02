package com.sptek.serviceName._global.util;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class ResponseUtil {

    public static HashMap<String, String> getResponseHeaderMap(HttpServletResponse response) {
        return getResponseHeaderMap(response, "");
    }

    public static HashMap<String, String> getResponseHeaderMap(HttpServletResponse response, String delimiter) {
        StringBuilder headerString = new StringBuilder();
        HashMap<String, String> headers = new HashMap<>();

        // 요청 헤더 이름을 가져오기
        Set<String> headerNames = TypeConvertUtil.collectionToSet(response.getHeaderNames());
        // 모든 헤더를 순회하며 로그로 남기기
        for (String headerName : headerNames) {
            Enumeration<String> headerValues = TypeConvertUtil.collectionToEnumeration(response.getHeaders(headerName));

            // 헤더 값을 리스트 형태로 변환하여 출력
            StringBuilder values = new StringBuilder();
            while (headerValues.hasMoreElements()) {
                values.append(headerValues.nextElement()).append(", ");
            }

            // 마지막 쉼표와 공백 제거
            if (values.length() > 0) {
                values.setLength(values.length() - 2);  // 마지막 쉼표와 공백 제거
            }

            // 최종 문자열에 추가
            //headerString.append(headerName).append(" = ").append(values.toString()).append("\n");
            headers.put(headerName, values.append(delimiter).toString());
        }
        return headers;
    }
}

