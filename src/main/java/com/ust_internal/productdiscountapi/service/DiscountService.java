package com.ust_internal.productdiscountapi.service;


import com.ust_internal.productdiscountapi.dto.DiscountRequestDto;
import com.ust_internal.productdiscountapi.dto.DiscountResponseDto;

public interface DiscountService {
    DiscountResponseDto calculateDiscount(DiscountRequestDto requestDto);
    DiscountResponseDto calculateDiscountAndSave(DiscountRequestDto requestDto);
}