package com.sptek._frameworkWebCore.util;

import com.sptek._frameworkWebCore.base.code.CommonErrorCodeEnum;
import com.sptek._frameworkWebCore.base.exception.ServiceException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

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

    public static ResponseEntity makeFileResponseEntity(String requestFile) throws Exception {
        return makeFileResponseEntity(requestFile, "storage.anyone.localRootPath");
    }


    public static ResponseEntity makeFileResponseEntityFromAnyone(String requestFile) throws Exception {
        return makeFileResponseEntity(requestFile, "storage.anyone.localRootPath");
    }

    public static ResponseEntity makeFileResponseEntityFromLoginUser(String requestFile) throws Exception {
        if (SecurityUtil.isRealLogin()) {
            return makeFileResponseEntity(requestFile, "storage.loginUser.localRootPath");
        } else {
          throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR, "필요한 접근 권한이 없습니다.");
        }
    }

    public static ResponseEntity makeFileResponseEntityFromSpecificUser(String requestFile) throws Exception {
        if (SecurityUtil.isRealLogin()) {
            String extraPath = String.valueOf(SecurityUtil.getMyCustomUserDetails().getUserDto().getId());
            return makeFileResponseEntity(requestFile, extraPath, "storage.specificUser.localRootPath");
        } else {
            throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR, "필요한 접근 권한이 없습니다.");
        }
    }

    public static ResponseEntity makeFileResponseEntityFromSpecificRole(String requestFile) throws Exception {
        if (SecurityUtil.isRealLogin()) {
            return makeFileResponseEntity(requestFile, "storage.specificRole.localRootPath");
        } else {
            throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR, "필요한 접근 권한이 없습니다.");
        }
    }

    public static ResponseEntity makeFileResponseEntityFromSpecificAuth(String requestFile) throws Exception {
        if (SecurityUtil.isRealLogin()) {
            return makeFileResponseEntity(requestFile, "storage.specificRole.localRootPath");
        } else {
            throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR, "필요한 접근 권한이 없습니다.");
        }
    }

    //현재 사용자 가 권한 형식의 주어진 Path 를 통과할 권한이 있는지 확인 함
    public static boolean hasAuthOnPath(String pathString) {
        if (StringUtils.isBlank(pathString))
            throw new ServiceException(CommonErrorCodeEnum.NOT_VALID_ERROR, "요청이 올바르지 않습니다.");

        Path path = Paths.get(pathString);
        if (path.getNameCount() == 0) throw new ServiceException(CommonErrorCodeEnum.NOT_VALID_ERROR, "path 가 올바르지 않습니다.");

        // 첫 번째 디렉토리 추출 (ex. role-admin-adminTop-system)
        String firstSegment = path.getName(0).toString();
        if (firstSegment.toLowerCase().startsWith("role-")) {
            Set<String> pathRoles = Arrays.stream(firstSegment.substring("role-".length()).split("-"))
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            return !Collections.disjoint(pathRoles, SecurityUtil.getMyRole());

        } else if (firstSegment.toLowerCase().startsWith("auth-")) {
            Set<String> pathRoles = Arrays.stream(firstSegment.substring("auth-".length()).split("-"))
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            return !Collections.disjoint(pathRoles, SecurityUtil.getMyAuth());

        } else {

        }
    }





    private static ResponseEntity makeFileResponseEntity(String requestFile, String storageKey) throws Exception {
        return makeFileResponseEntity(requestFile, "", storageKey);
    }

    private static ResponseEntity makeFileResponseEntity(@Nullable String requestFile) throws Exception {
        if (StringUtils.isBlank(requestFile))
            throw new ServiceException(CommonErrorCodeEnum.NOT_VALID_ERROR, "요청이 올바르지 않습니다.");

        requestFile = URLDecoder.decode(requestFile, StandardCharsets.UTF_8);
        Path path = Paths.get(requestFile);
        if (path.getNameCount() == 0)
            throw new ServiceException(CommonErrorCodeEnum.NOT_VALID_ERROR, "path 가 올바르지 않습니다.");

        // 첫 번째 디렉토리 추출 (ex. role-admin-adminTop-system)
        String checkDirName = path.getName(0).toString();
        File realFile = null;
        if (checkDirName.equalsIgnoreCase("login")) {
            if (!SecurityUtil.isRealLogin())
                throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR, "필요한 접근 권한이 없습니다.");
            realFile = new File(Path.of(String.valueOf(SpringUtil.getApplicationProperty("storage.loginUser.localRootPath")), requestFile).toString());

        } else if (checkDirName.toLowerCase().startsWith("role-")) {
            Set<String> pathRoles = Arrays.stream(checkDirName.substring("role-".length()).split("-")).map(String::toLowerCase).collect(Collectors.toSet());
            if (Collections.disjoint(pathRoles, SecurityUtil.getMyRole()))
                throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR, "필요한 접근 권한이 없습니다.");
            realFile = new File(Path.of(String.valueOf(SpringUtil.getApplicationProperty("storage..localRootPath")), requestFile).toString());

        } else if (firstSegment.toLowerCase().startsWith("auth-")) {
            Set<String> pathRoles = Arrays.stream(firstSegment.substring("auth-".length()).split("-"))
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            return !Collections.disjoint(pathRoles, getMyAuth());

        } else {
            return true;
        }


        try {
            requestFile = URLDecoder.decode(requestFile, StandardCharsets.UTF_8);






            File file = new File(Path.of(String.valueOf(SpringUtil.getApplicationProperty(storageKey)), extraPath, requestFile).toString());
            log.debug("final request file: {}", file.getAbsolutePath());
            HttpHeaders header = new HttpHeaders();
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            return new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);

        } catch (Exception e) {
            throw new ServiceException(CommonErrorCodeEnum.FORBIDDEN_ERROR, "필요한 접근 권한이 없습니다.");
        }
    }
}

