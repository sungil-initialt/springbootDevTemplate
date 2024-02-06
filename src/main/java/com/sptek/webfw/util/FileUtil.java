package com.sptek.webfw.util;

import com.sptek.webfw.code.ApiErrorCode;
import com.sptek.webfw.exception.ApiServiceException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FileUtil {

    public static List<UploadFileDto> uploadFile(MultipartFile[] uploadFiles, Predicate<MultipartFile> throwExceptionFilter) {
        List<UploadFileDto> uploadFileDtoList = new ArrayList<>();

        for (MultipartFile uploadFile : uploadFiles) {
            if(throwExceptionFilter.test(uploadFile)) {
                throw new ApiServiceException(ApiErrorCode.FORBIDDEN_ERROR);
            }


        }
        //--->여기처리

        return  uploadFileDtoList;
    }

    public static String extractFileNameOnly(String filePath) {
        Path path = Paths.get(filePath);
        Path fileName = path.getFileName();
        return fileName.toString();
    }

    @Data
    @AllArgsConstructor
    @Slf4j
    public class UploadFileDto {
        private String originFileName;
        private String uuidForAvoidDuplication;
        private String serverStorageRoot;

        public String getUploadedImgUrl() {
            return URLEncoder.encode(serverStorageRoot + File.separator +  uuidForAvoidDuplication + File.separator + originFileName, StandardCharsets.UTF_8);
        }
    }
}
