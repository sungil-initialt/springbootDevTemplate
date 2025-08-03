package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.code.CommonErrorCodeEnum;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static ResponseEntity<byte[]> makeResponseEntityFromFile(Path securedFilePath) throws Exception {
        if (securedFilePath == null) throw new IllegalArgumentException("securedFilePath is required");
        if (!SecurityUtil.hasPermissionForSecuredFilePath(securedFilePath)) {
            throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR, "필요한 접근 권한이 없습니다.");
        }

        Path resolvedPath = SecurityUtil.getStorageRootPath(securedFilePath).resolve(securedFilePath);
        File finalFile = resolvedPath.toFile();
        //log.debug("Final request file: {}", finalFile.getAbsolutePath());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", Files.probeContentType(resolvedPath));

        byte[] fileBytes = FileCopyUtils.copyToByteArray(finalFile);
        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }

    public static String getResponseBody(ContentCachingResponseWrapper responseWrapper) {
        byte[] content = responseWrapper.getContentAsByteArray();
        if (content.length == 0) return "No Content";
        if (content.length > 3000_0) return "-> The body is too big and skipped"; // 30K

        try {
            return new String(content, responseWrapper.getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            return "Unsupported Encoding";
        }
    }
}
