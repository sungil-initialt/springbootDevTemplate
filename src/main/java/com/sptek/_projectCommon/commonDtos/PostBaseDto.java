package com.sptek._projectCommon.commonDtos;

import lombok.Data;

import java.util.List;

@Data
public abstract class PostBaseDto {
    private Long boardId = 1L;
    private Long postId;
    private String boardName = "POST_EX";

    private String createAt;
    private String updateAt;

    private Long userId = 1L;
    private String userEmail = "sungilry@naver.com";
    private String userName = "이성일1";

    private List<UploadFileDto> uploadFileDtos;
}
