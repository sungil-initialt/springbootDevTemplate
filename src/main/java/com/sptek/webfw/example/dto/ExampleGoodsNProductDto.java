package com.sptek.webfw.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleGoodsNProductDto {
    private ExampleGoodsDto exampleGoodsDto;
    private ExampleProductDto exampleProductDto;
    private String goodsName;
    private String productName;
}
