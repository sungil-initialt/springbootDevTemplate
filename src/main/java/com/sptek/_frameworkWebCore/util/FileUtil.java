package com.sptek._frameworkWebCore.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class FileUtil {

    //파일경로+파일명 구조에서 파일명만 추출(확장자포함)
    public static String extractFileNameOnly(String fileNameWithPath) {
        Path path = Paths.get(fileNameWithPath);
        Path fileNameOnly = path.getFileName();
        return fileNameOnly.toString();
    }

    //주어진 파일경로대로 디렉토리를 구성함(이미 존재하는 경로여도 상관없음)
    public static void createDirectories(Path directories) throws IOException {
        if (directories != null) {
            //FileAttribute<?> fileAttrs = PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x"));
            //Files.createDirectories(parentDir, fileAttrs);
            Files.createDirectories(directories);
        }
        log.debug("dir path : " + directories);
    }

}
