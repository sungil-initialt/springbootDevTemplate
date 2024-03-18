package com.sptek.webfw.util;

import com.sptek.webfw.code.ErrorCode;
import com.sptek.webfw.example.dto.FileUploadDto;
import com.sptek.webfw.exceptionHandler.exception.ServiceException;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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
            , @Nullable String additionalPath
            , @Nullable Predicate<MultipartFile> exceptionFilter) throws ServiceException, IOException{

        additionalPath = Optional.ofNullable(additionalPath).orElse("");
        List<FileUploadDto> uploadFileDtoList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            //예외 조건 확인
            if(exceptionFilter != null && exceptionFilter.test(multipartFile)) {
                //exception 조건에 맞는경우 ex를 발생시켜 줌
                throw ServiceException.builder().errorCode(ErrorCode.FORBIDDEN_ERROR).build();
            }

            //브라우저에따라 파일명에 경로가 포함되는 경우가 있어 제거 추가
            String originFileName = extractFileNameOnly(multipartFile.getOriginalFilename());
            String newFilePath = baseStoragePath + File.separator +  LocalDate.now().getYear()
                    + File.separator + LocalDate.now().getMonthValue()
                    + File.separator + LocalDate.now().getDayOfMonth();
            newFilePath = (additionalPath.isBlank()) ? newFilePath + File.separator : newFilePath + File.separator + additionalPath + File.separator;

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
    public static void createDirectories(String directories) throws IOException {
        //directories = directories.replaceAll("//", "/");
        Path dirPath = Paths.get(directories);
        //Path parentDir = dirPath.getParent();

        if (dirPath != null) {
            //FileAttribute<?> fileAttrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x"));
            //Files.createDirectories(parentDir, fileAttrs);
            Files.createDirectories(dirPath);
        }
        log.debug("Created directories for path: " + dirPath);
    }

}
