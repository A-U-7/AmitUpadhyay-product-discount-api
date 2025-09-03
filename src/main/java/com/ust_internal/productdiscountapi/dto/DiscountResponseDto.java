package com.ust_internal.productdiscountapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountResponseDto {
    private List<DiscountedProductDto> discountedProducts;
    private Double totalSavings;
    private Double finalBill;
}