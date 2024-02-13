package com.sptek.webfw.util;

import com.sptek.webfw.code.ApiErrorCode;
import com.sptek.webfw.example.dto.FileUploadDto;
import com.sptek.webfw.exception.ApiServiceException;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

@Slf4j
public class FileUtil {

    public static List<FileUploadDto> saveMultipartFiles(MultipartFile[] multipartFiles
            , String baseStoragePath
            , @Nullable String additionalLastPath
            , @Nullable Predicate<MultipartFile> exceptionFilter) throws Exception {

        additionalLastPath = Optional.ofNullable(additionalLastPath).orElse("");
        List<FileUploadDto> uploadFileDtoList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            //예외 조건 확인
            if(exceptionFilter != null && exceptionFilter.test(multipartFile)) {
                throw new ApiServiceException(ApiErrorCode.FORBIDDEN_ERROR);
            }

            //브라우저에따라 파일명에 경로가 포함되는 경우가 있어 제거 추가
            String originFileName = extractFileNameOnly(multipartFile.getOriginalFilename());
            String newFilePath = baseStoragePath + File.separator +  LocalDate.now().getYear()
                    + File.separator + LocalDate.now().getMonthValue()
                    + File.separator + LocalDate.now().getDayOfMonth();
                    //+ File.separator + additionalLastPath
                    //+ File.separator;

            createDirectories(newFilePath);
            String uuidForFileName = UUID.randomUUID().toString();
            Path finalPathNname = Paths.get(newFilePath + uuidForFileName + "_" + originFileName);

            multipartFile.transferTo(finalPathNname);
            uploadFileDtoList.add(new FileUploadDto(uuidForFileName, originFileName));
        }

        return  uploadFileDtoList;
    }

    //파일경로+파일명 구조에서 파일명만 추출(확장자포함)
    public static String extractFileNameOnly(String fileNameWithPath) {
        Path path = Paths.get(fileNameWithPath);
        Path fileNameOnly = path.getFileName();
        return fileNameOnly.toString();
    }

    //주어진 파일경로대로 디렉토리를 구성함(이미 존재하는 경로여도 상관없음)
    public static void createDirectories(String filePath) throws Exception{
        Path path = Paths.get(filePath);
        Path parentDir = path.getParent();

        if (parentDir != null) {
            //FileAttribute<?> fileAttrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x"));
            //Files.createDirectories(parentDir, fileAttrs);
            Files.createDirectories(parentDir);

            log.debug("Created directories for path: " + parentDir);
        }
    }

}
