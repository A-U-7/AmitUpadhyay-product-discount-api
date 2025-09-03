package com.ust_internal.productdiscountapi.service;


import com.ust_internal.productdiscountapi.dto.DiscountRequestDto;
import com.ust_internal.productdiscountapi.dto.DiscountResponseDto;
import com.ust_internal.productdiscountapi.dto.DiscountedProductDto;
import com.ust_internal.productdiscountapi.dto.ProductDto;
import com.ust_internal.productdiscountapi.entity.ProductEntity;
import com.ust_internal.productdiscountapi.exception.InvalidProductException;
import com.ust_internal.productdiscountapi.mapper.ProductMapper;
import com.ust_internal.productdiscountapi.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements DiscountService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public DiscountResponseDto calculateDiscount(DiscountRequestDto requestDto) {
        List<DiscountedProductDto> discountedProducts = requestDto.getProducts().stream()
                .map(this::applyDiscount)
                .collect(Collectors.toList());

        return buildResponse(discountedProducts);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiscountResponseDto calculateDiscountAndSave(DiscountRequestDto requestDto) {
        if (requestDto == null || requestDto.getProducts() == null) {
            throw new IllegalArgumentException("Request DTO and products must not be null");
        }

        // Validate all products before saving
        requestDto.getProducts().forEach(this::validateProduct);

        try {
            // Map DTOs to entities and save
            List<ProductEntity> productEntities = requestDto.getProducts().stream()
                    .map(productMapper::toEntity)
                    .collect(Collectors.toList());

            List<ProductEntity> savedEntities = productRepository.saveAll(productEntities);

            // Map saved entities back to DTOs for consistency
            List<ProductDto> savedProductDto = savedEntities.stream()
                    .map(productMapper::toDto)
                    .collect(Collectors.toList());

            // Create new request with saved products (including generated IDs)
            DiscountRequestDto savedRequest = new DiscountRequestDto(savedProductDto);

            // Calculate and return discount
            return calculateDiscount(savedRequest);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save products and calculate discount", e);
        }
    }

    DiscountedProductDto applyDiscount(ProductDto product) {
        validateProduct(product);
        
        double originalPrice = product.getPrice() * product.getQuantity();
        double finalPrice = originalPrice;
        double savings = 0.0;

        switch (product.getCategory().toLowerCase()) {
            case "electronics" -> {
                if (product.getPrice() >= 20000) {
                    savings = originalPrice * 0.10;
                    finalPrice = originalPrice - savings;
                }
            }
            case "clothing" -> {
                if (product.getQuantity() >= 3) {
                    int freeItems = product.getQuantity() / 3;
                    savings = freeItems * product.getPrice();
                    finalPrice = originalPrice - savings;
                }
            }
            case "grocery" -> {
                if (product.getQuantity() >= 10) {
                    savings = originalPrice * 0.05;
                    finalPrice = originalPrice - savings;
                }
            }
        }

        return new DiscountedProductDto(
                product.getId(),
                product.getName(),
                finalPrice,
                savings
        );
    }

    void validateProduct(ProductDto product) {
        if (product.getQuantity() <= 0 || product.getPrice() <= 0) {
            throw new InvalidProductException(
                    String.format("Invalid product: %s. Quantity and price must be greater than 0", product.getName())
            );
        }
    }

    private DiscountResponseDto buildResponse(List<DiscountedProductDto> discountedProducts) {
        double totalSavings = discountedProducts.stream()
                .mapToDouble(DiscountedProductDto::getSavings)
                .sum();

        double finalBill = discountedProducts.stream()
                .mapToDouble(DiscountedProductDto::getFinalPrice)
                .sum();

        return new DiscountResponseDto(discountedProducts, totalSavings, finalBill);
    }
}