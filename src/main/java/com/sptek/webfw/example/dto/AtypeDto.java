package com.sptek.webfw.example.dto;

import lombok.Data;

@Data
public class AtypeDto {
    private String manufacturerName = "Samsung";
    private String productName = "TV";
    private long productPrice = 1000000L;
    private long discountRate = 20L;
    private boolean availableReturn = true;
}
