package com.sptek._projectCommon.commonDtos;

import lombok.Data;

@Data
public class UploadFileDto {
    private long postId;
    private String filePath;
    private String fileName;
    private int fileOrder;
}

