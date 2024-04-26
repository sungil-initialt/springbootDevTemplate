package com.sptek.webfw.example.dto;

import lombok.Data;

@Data
public class ExampleGoodsDto {
    private String manufacturerName;
    private String name;
    private long originPrice = 0;
    private int weight = 1;
    private int discountedPrice;
    private int stock;
    private String availableSendBackYn;
}
