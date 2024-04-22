package com.sptek.webfw.example.dto;

import lombok.Data;

@Data
public class BtypeDto {
    /*
    private String ;
    private String name;
    private String price;
    private String discountedPrice;
    private String returnYn;
     */

    private String manufacturerName; //타입,변수명 동일하여 custom 필요없음
    private String name; //변수명 달라서 커스텀 함
    private String productPrice; //변수명 동일하고 타입은 다르지만 커스텀 필요없음
    private String discountedPrice;
    private String availableReturn;
}
