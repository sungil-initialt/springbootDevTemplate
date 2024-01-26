package com.sptek.webfw.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
DTO는 가능하면 builder 방식으로 사용하면 좋음
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TBTestDto {
    private int c1;
    private int c2;
    private int c3;

}
