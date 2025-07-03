package com.sptek._projectCommon.commonObject.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class FileStorageDto {
    final Path rootPath;
    final Path authPath; //권한 설정에 따른 서브 Path
}
