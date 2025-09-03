package com.ust_internal.productdiscountapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountedProductDto {
    private Long id;
    private String name;
    private Double finalPrice;
    private Double savings;
}