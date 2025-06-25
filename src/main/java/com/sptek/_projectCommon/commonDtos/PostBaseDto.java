package com.sptek._projectCommon.commonDtos;

import lombok.Data;

@Data
public abstract class PostBaseDto {
    private Long boardId = 1L;
    private String boardName = "POST_EX";
    private Long postId;
    private String createAt;
    private String updateAt;

    private Long userId = 1L;
    private String userEmail = "sungilry@naver.com";
    private String userName = "이성일1";
}
