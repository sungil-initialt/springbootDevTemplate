package com.sptek.webfw.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
@Slf4j
public class FileUploadDto {
    private String originFileName;
    private String uuidForAvoidDuplication;
    private String serverStorageRoot;

    public String getUploadedImgUrl() {
        return URLEncoder.encode(serverStorageRoot + File.separator +  uuidForAvoidDuplication + File.separator + originFileName, StandardCharsets.UTF_8);
    }
}
