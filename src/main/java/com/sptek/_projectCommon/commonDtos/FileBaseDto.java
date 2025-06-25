package com.sptek._projectCommon.commonDtos;

import lombok.Data;

@Data
public class FileBaseDto {
    private long ownerId;
    private String originalFileName;
    private String realFileName;
    private int fileOrder;

}

