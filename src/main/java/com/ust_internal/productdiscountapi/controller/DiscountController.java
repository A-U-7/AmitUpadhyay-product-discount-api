package com.ust_internal.productdiscountapi.controller;


import com.ust_internal.productdiscountapi.dto.DiscountRequestDto;
import com.ust_internal.productdiscountapi.dto.DiscountResponseDto;
import com.ust_internal.productdiscountapi.service.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping("/discount")
    public ResponseEntity<DiscountResponseDto> calculateDiscount(
            @Valid @RequestBody DiscountRequestDto requestDto) {

        DiscountResponseDto response = discountService.calculateDiscount(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/discount/save")
    public ResponseEntity<DiscountResponseDto> calculateDiscountAndSave(
            @Valid @RequestBody DiscountRequestDto requestDto) {

        DiscountResponseDto response = discountService.calculateDiscountAndSave(requestDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}