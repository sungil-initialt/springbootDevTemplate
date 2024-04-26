package com.sptek.webfw.example.dto;

import lombok.Data;

@Data
public class ExampleProductDto {
    private String manufacturerName = "Samsung";
    private String productName = "TV";
    private long productPrice = 1000000L;
    private int weight;
    private int curDiscountRate = 20;
    private int quantity = 1000;
    private boolean isAvailableReturn = true;


//    public long getDiscountedPrice(){
//        return getProductPrice() * (100 - getDiscountRate()) / 100;
//    }
}
