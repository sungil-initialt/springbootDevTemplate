package com.sptek._projectCommon.commonDtos;

import lombok.Data;

@Data
public class UploadFileDto {
    private Long postId;
    private Integer boardId;

    private String fileName;
    private Integer fileOrder;
    private String filePath; //서버 에서 생성됨
}

