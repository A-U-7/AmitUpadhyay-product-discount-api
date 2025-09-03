package com.ust_internal.productdiscountapi.service;


import com.ust_internal.productdiscountapi.dto.DiscountRequestDto;
import com.ust_internal.productdiscountapi.dto.DiscountResponseDto;
import com.ust_internal.productdiscountapi.dto.DiscountedProductDto;
import com.ust_internal.productdiscountapi.dto.ProductDto;
import com.ust_internal.productdiscountapi.entity.ProductEntity;
import com.ust_internal.productdiscountapi.exception.InvalidProductException;
import com.ust_internal.productdiscountapi.mapper.ProductMapper;
import com.ust_internal.productdiscountapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscountServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private DiscountServiceImpl discountService;

    private ProductDto electronicsProduct;
    private ProductDto clothingProduct;
    private ProductDto groceryProduct;
    private ProductDto invalidProduct;

    @BeforeEach
    void setUp() {
        electronicsProduct = new ProductDto(1L, "Laptop", "Electronics", 50000.0, 1);
        clothingProduct = new ProductDto(2L, "Shirt", "Clothing", 1000.0, 3);
        groceryProduct = new ProductDto(3L, "Apple", "Grocery", 100.0, 12);
        invalidProduct = new ProductDto(4L, "Invalid", "Electronics", -100.0, 0);
    }

    @Test
    void calculateDiscount_WithValidProducts_ReturnsCorrectDiscountResponse() {
        // Given
        DiscountRequestDto requestDto = new DiscountRequestDto(
                Arrays.asList(electronicsProduct, clothingProduct, groceryProduct)
        );

        // When
        DiscountResponseDto response = discountService.calculateDiscount(requestDto);

        // Then
        assertNotNull(response);
        assertEquals(3, response.getDiscountedProducts().size());
        assertEquals(6060.0, response.getTotalSavings(), 0.001);
        assertEquals(48140.0, response.getFinalBill(), 0.001);

        // Verify electronics discount
        DiscountedProductDto electronicsResult = response.getDiscountedProducts().get(0);
        assertEquals(1L, electronicsResult.getId());
        assertEquals(45000.0, electronicsResult.getFinalPrice(), 0.001);
        assertEquals(5000.0, electronicsResult.getSavings(), 0.001);

        // Verify clothing discount
        DiscountedProductDto clothingResult = response.getDiscountedProducts().get(1);
        assertEquals(2L, clothingResult.getId());
        assertEquals(2000.0, clothingResult.getFinalPrice(), 0.001);
        assertEquals(1000.0, clothingResult.getSavings(), 0.001);

        // Verify grocery discount
        DiscountedProductDto groceryResult = response.getDiscountedProducts().get(2);
        assertEquals(3L, groceryResult.getId());
        assertEquals(1140.0, groceryResult.getFinalPrice(), 0.001);
        assertEquals(60.0, groceryResult.getSavings(), 0.001);
    }

    @Test
    void calculateDiscount_WithInvalidProduct_ThrowsInvalidProductException() {
        // Given
        DiscountRequestDto requestDto = new DiscountRequestDto(List.of(invalidProduct));

        // When & Then
        assertThrows(InvalidProductException.class, () -> {
            discountService.calculateDiscount(requestDto);
        });
    }

    @Test
    void calculateDiscount_WithNoDiscountProducts_ReturnsOriginalPrices() {
        // Given
        ProductDto noDiscountElectronics = new ProductDto(1L, "Mouse", "Electronics", 5000.0, 1);
        ProductDto noDiscountClothing = new ProductDto(2L, "Sock", "Clothing", 500.0, 2);
        ProductDto noDiscountGrocery = new ProductDto(3L, "Banana", "Grocery", 50.0, 5);

        DiscountRequestDto requestDto = new DiscountRequestDto(
                Arrays.asList(noDiscountElectronics, noDiscountClothing, noDiscountGrocery)
        );

        // When
        DiscountResponseDto response = discountService.calculateDiscount(requestDto);

        // Then
        assertNotNull(response);
        assertEquals(3, response.getDiscountedProducts().size());
        assertEquals(0.0, response.getTotalSavings(), 0.001);
        assertEquals(5000.0 + 1000.0 + 250.0, response.getFinalBill(), 0.001);
    }

    @Test
    void calculateDiscount_WithMultipleClothingItems_AppliesBuy2Get1FreeCorrectly() {
        // Given
        ProductDto clothing6Items = new ProductDto(1L, "T-Shirt", "Clothing", 500.0, 6);
        DiscountRequestDto requestDto = new DiscountRequestDto(List.of(clothing6Items));

        // When
        DiscountResponseDto response = discountService.calculateDiscount(requestDto);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getDiscountedProducts().size());
        
        DiscountedProductDto result = response.getDiscountedProducts().get(0);
        assertEquals(2000.0, result.getFinalPrice(), 0.001); // 4 paid items * 500
        assertEquals(1000.0, result.getSavings(), 0.001);    // 2 free items * 500
    }

    @Test
    void calculateDiscountAndSave_WithValidProducts_SavesProductsAndReturnsDiscount() {
        // Given
        DiscountRequestDto requestDto = new DiscountRequestDto(
                Arrays.asList(electronicsProduct, clothingProduct)
        );

        ProductEntity electronicsEntity = new ProductEntity();
        electronicsEntity.setName("Laptop");
        electronicsEntity.setCategory("Electronics");
        electronicsEntity.setPrice(50000.0);
        electronicsEntity.setQuantity(1);

        ProductEntity clothingEntity = new ProductEntity();
        clothingEntity.setName("Shirt");
        clothingEntity.setCategory("Clothing");
        clothingEntity.setPrice(1000.0);
        clothingEntity.setQuantity(3);

        ProductDto electronicsDto = new ProductDto(1L, "Laptop", "Electronics", 50000.0, 1);
        ProductDto clothingDto = new ProductDto(2L, "Shirt", "Clothing", 1000.0, 3);
        
        when(productMapper.toEntity(any(ProductDto.class)))
                .thenAnswer(invocation -> {
                    ProductDto dto = invocation.getArgument(0);
                    if (dto.getName().equals("Laptop")) {
                        return electronicsEntity;
                    } else {
                        return clothingEntity;
                    }
                });
        
        when(productMapper.toDto(any(ProductEntity.class)))
                .thenAnswer(invocation -> {
                    ProductEntity entity = invocation.getArgument(0);
                    if (entity.getName().equals("Laptop")) {
                        return electronicsDto;
                    } else {
                        return clothingDto;
                    }
                });
        
        when(productRepository.saveAll(anyList()))
                .thenReturn(Arrays.asList(electronicsEntity, clothingEntity));

        // When
        DiscountResponseDto response = discountService.calculateDiscountAndSave(requestDto);

        // Then
        assertNotNull(response);
        assertEquals(2, response.getDiscountedProducts().size());
        
        verify(productMapper, times(2)).toEntity(any(ProductDto.class));
        verify(productMapper, times(2)).toDto(any(ProductEntity.class));
        verify(productRepository, times(1)).saveAll(anyList());
    }

    @Test
    void applyDiscount_ElectronicsPriceBelowThreshold_NoDiscountApplied() {
        // Given
        ProductDto cheapElectronics = new ProductDto(1L, "Mouse", "Electronics", 19999.0, 1);

        // When
        DiscountedProductDto result = discountService.applyDiscount(cheapElectronics);

        // Then
        assertEquals(19999.0, result.getFinalPrice(), 0.001);
        assertEquals(0.0, result.getSavings(), 0.001);
    }

    @Test
    void applyDiscount_ElectronicsPriceAtThreshold_DiscountApplied() {
        // Given
        ProductDto thresholdElectronics = new ProductDto(1L, "TV", "Electronics", 20000.0, 2);

        // When
        DiscountedProductDto result = discountService.applyDiscount(thresholdElectronics);

        // Then
        assertEquals(36000.0, result.getFinalPrice(), 0.001); // 40000 - 10%
        assertEquals(4000.0, result.getSavings(), 0.001);     // 10% of 40000
    }

    @Test
    void applyDiscount_ClothingQuantityBelowThreshold_NoDiscountApplied() {
        // Given
        ProductDto lowQuantityClothing = new ProductDto(1L, "Hat", "Clothing", 300.0, 2);

        // When
        DiscountedProductDto result = discountService.applyDiscount(lowQuantityClothing);

        // Then
        assertEquals(600.0, result.getFinalPrice(), 0.001);
        assertEquals(0.0, result.getSavings(), 0.001);
    }

    @Test
    void applyDiscount_GroceryQuantityBelowThreshold_NoDiscountApplied() {
        // Given
        ProductDto lowQuantityGrocery = new ProductDto(1L, "Orange", "Grocery", 50.0, 9);

        // When
        DiscountedProductDto result = discountService.applyDiscount(lowQuantityGrocery);

        // Then
        assertEquals(450.0, result.getFinalPrice(), 0.001);
        assertEquals(0.0, result.getSavings(), 0.001);
    }

    @Test
    void applyDiscount_UnknownCategory_NoDiscountApplied() {
        // Given
        ProductDto unknownCategory = new ProductDto(1L, "Book", "Books", 100.0, 5);

        // When
        DiscountedProductDto result = discountService.applyDiscount(unknownCategory);

        // Then
        assertEquals(500.0, result.getFinalPrice(), 0.001);
        assertEquals(0.0, result.getSavings(), 0.001);
    }

    @Test
    void applyDiscount_CategoryCaseInsensitive_DiscountApplied() {
        // Given
        ProductDto electronicsUpperCase = new ProductDto(1L, "Laptop", "ELECTRONICS", 50000.0, 1);
        ProductDto clothingMixedCase = new ProductDto(2L, "Shirt", "ClOtHiNg", 1000.0, 3);
        ProductDto groceryLowerCase = new ProductDto(3L, "Apple", "grocery", 100.0, 12);

        // When & Then
        DiscountedProductDto electronicsResult = discountService.applyDiscount(electronicsUpperCase);
        assertEquals(45000.0, electronicsResult.getFinalPrice(), 0.001);

        DiscountedProductDto clothingResult = discountService.applyDiscount(clothingMixedCase);
        assertEquals(2000.0, clothingResult.getFinalPrice(), 0.001);

        DiscountedProductDto groceryResult = discountService.applyDiscount(groceryLowerCase);
        assertEquals(1140.0, groceryResult.getFinalPrice(), 0.001);
    }

    @Test
    void validateProduct_WithValidProduct_DoesNotThrowException() {
        // Given
        ProductDto validProduct = new ProductDto(1L, "Valid", "Electronics", 100.0, 5);

        // When & Then
        assertDoesNotThrow(() -> discountService.validateProduct(validProduct));
    }

    @Test
    void validateProduct_WithZeroPrice_ThrowsInvalidProductException() {
        // Given
        ProductDto zeroPriceProduct = new ProductDto(1L, "Invalid", "Electronics", 0.0, 5);

        // When & Then
        assertThrows(InvalidProductException.class, () -> discountService.validateProduct(zeroPriceProduct));
    }

    @Test
    void validateProduct_WithNegativePrice_ThrowsInvalidProductException() {
        // Given
        ProductDto negativePriceProduct = new ProductDto(1L, "Invalid", "Electronics", -100.0, 5);

        // When & Then
        assertThrows(InvalidProductException.class, () -> discountService.validateProduct(negativePriceProduct));
    }

    @Test
    void validateProduct_WithZeroQuantity_ThrowsInvalidProductException() {
        // Given
        ProductDto zeroQuantityProduct = new ProductDto(1L, "Invalid", "Electronics", 100.0, 0);

        // When & Then
        assertThrows(InvalidProductException.class, () -> discountService.validateProduct(zeroQuantityProduct));
    }

    @Test
    void validateProduct_WithNegativeQuantity_ThrowsInvalidProductException() {
        // Given
        ProductDto negativeQuantityProduct = new ProductDto(1L, "Invalid", "Electronics", 100.0, -5);

        // When & Then
        assertThrows(InvalidProductException.class, () -> discountService.validateProduct(negativeQuantityProduct));
    }
}