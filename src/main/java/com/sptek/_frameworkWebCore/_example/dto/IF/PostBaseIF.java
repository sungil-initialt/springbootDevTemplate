package com.sptek._frameworkWebCore._example.dto.IF;

import lombok.Data;

@Data
public abstract class PostBaseIF {
    private Long postId;
    private String createAt;
    private String updateAt;

    private Long userId;
    private String userEmail;
    private String userName;

}