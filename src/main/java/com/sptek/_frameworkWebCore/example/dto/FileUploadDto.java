package com.sptek._frameworkWebCore.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
@Slf4j
public class FileUploadDto implements Serializable {
    private String uuidForFileName;
    private String originFileName;

    public String getUuidForFileName() {
        return URLEncoder.encode(uuidForFileName, StandardCharsets.UTF_8);
    }

    public String getOriginFileName() {
        return URLEncoder.encode(originFileName, StandardCharsets.UTF_8);
    }
}
