package com.ust_internal.productdiscountapi.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountRequestDto {
    @Valid
    @NotNull(message = "Products list is required")
    @Size(min = 1, message = "At least one product is required")
    private List<ProductDto> products;
}