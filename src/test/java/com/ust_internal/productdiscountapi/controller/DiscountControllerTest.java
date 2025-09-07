package com.ust_internal.productdiscountapi.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ust_internal.productdiscountapi.dto.DiscountRequestDto;
import com.ust_internal.productdiscountapi.dto.DiscountResponseDto;
import com.ust_internal.productdiscountapi.dto.DiscountedProductDto;
import com.ust_internal.productdiscountapi.dto.ProductDto;
import com.ust_internal.productdiscountapi.service.DiscountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DiscountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DiscountService discountService;

    @InjectMocks
    private DiscountController discountController;

    private ObjectMapper objectMapper;
    private DiscountRequestDto validRequestDto;
    private DiscountResponseDto mockResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(discountController).build();
        objectMapper = new ObjectMapper();

        // Setup test data
        ProductDto product1 = new ProductDto(1L, "Laptop", "Electronics", 50000.0, 1);
        ProductDto product2 = new ProductDto(2L, "Shirt", "Clothing", 1000.0, 3);
        ProductDto product3 = new ProductDto(3L, "Apple", "Grocery", 100.0, 12);
        validRequestDto = new DiscountRequestDto(Arrays.asList(product1, product2, product3));

        DiscountedProductDto discounted1 = new DiscountedProductDto(1L, "Laptop", 45000.0, 5000.0);
        DiscountedProductDto discounted2 = new DiscountedProductDto(2L, "Shirt", 2000.0, 1000.0);
        DiscountedProductDto discounted3 = new DiscountedProductDto(3L, "Apple", 1140.0, 60.0);
        mockResponse = new DiscountResponseDto(
                Arrays.asList(discounted1, discounted2, discounted3),
                6060.0,
                48140.0
        );
    }

    @Test
    void calculateDiscount_WithValidRequest_ReturnsCreateResponse() throws Exception {
        // Given
        when(discountService.calculateDiscount(any(DiscountRequestDto.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/products/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.discountedProducts.length()").value(3))
                .andExpect(jsonPath("$.totalSavings").value(6060.0))
                .andExpect(jsonPath("$.finalBill").value(48140.0))
                .andExpect(jsonPath("$.discountedProducts[0].id").value(1))
                .andExpect(jsonPath("$.discountedProducts[0].name").value("Laptop"))
                .andExpect(jsonPath("$.discountedProducts[0].finalPrice").value(45000.0))
                .andExpect(jsonPath("$.discountedProducts[0].savings").value(5000.0));
    }

    @Test
    void calculateDiscountAndSave_WithValidRequest_ReturnsCreateResponse() throws Exception {
        // Given
        when(discountService.calculateDiscountAndSave(any(DiscountRequestDto.class))).thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/products/discount/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.discountedProducts.length()").value(3))
                .andExpect(jsonPath("$.totalSavings").value(6060.0))
                .andExpect(jsonPath("$.finalBill").value(48140.0));
    }

    @Test
    void calculateDiscount_WithEmptyProducts_ReturnsBadRequest() throws Exception {
        // Given
        DiscountRequestDto emptyRequest = new DiscountRequestDto(List.of());

        // When & Then
        mockMvc.perform(post("/api/v1/products/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateDiscount_WithInvalidProduct_ReturnsBadRequest() throws Exception {
        // Given - Product with negative price
        ProductDto invalidProduct = new ProductDto(1L, "Invalid", "Electronics", -100.0, 1);
        DiscountRequestDto invalidRequest = new DiscountRequestDto(List.of(invalidProduct));

        // When & Then
        mockMvc.perform(post("/api/v1/products/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateDiscount_WithMissingRequiredFields_ReturnsBadRequest() throws Exception {
        // Given - Missing name and category
        String invalidJson = """
                {
                    "products": [
                        {
                            "price": 100.0,
                            "quantity": 1
                        }
                    ]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/products/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateDiscount_WithNullRequest_ReturnsBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/products/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateDiscount_WithMalformedJson_ReturnsBadRequest() throws Exception {
        // Given
        String malformedJson = """
                {
                    "products": [
                        {
                            "name": "Test",
                            "category": "Electronics",
                            "price": "invalid",  // String instead of number
                            "quantity": 1
                        }
                    ]
                }
                """;

        // When & Then
        mockMvc.perform(post("/api/v1/products/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateDiscountAndSave_WithEmptyProducts_ReturnsBadRequest() throws Exception {
        // Given
        DiscountRequestDto emptyRequest = new DiscountRequestDto(List.of());

        // When & Then
        mockMvc.perform(post("/api/v1/products/discount/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateDiscount_WithDifferentContentType_ReturnsUnsupportedMediaType() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/products/discount")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("test content"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void calculateDiscount_WithValidRequestWithoutIds_ReturnsOkResponse() throws Exception {
        // Given - Products without IDs (for /discount/save endpoint)
        ProductDto product1 = new ProductDto(null, "Laptop", "Electronics", 50000.0, 1);
        ProductDto product2 = new ProductDto(null, "Shirt", "Clothing", 1000.0, 3);
        DiscountRequestDto requestWithoutIds = new DiscountRequestDto(Arrays.asList(product1, product2));

        DiscountedProductDto discounted1 = new DiscountedProductDto(null, "Laptop", 45000.0, 5000.0);
        DiscountedProductDto discounted2 = new DiscountedProductDto(null, "Shirt", 2000.0, 1000.0);
        DiscountResponseDto responseWithoutIds = new DiscountResponseDto(
                Arrays.asList(discounted1, discounted2),
                6000.0,
                47000.0
        );

        when(discountService.calculateDiscount(any(DiscountRequestDto.class))).thenReturn(responseWithoutIds);

        // When & Then
        mockMvc.perform(post("/api/v1/products/discount")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestWithoutIds)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.discountedProducts[0].id").doesNotExist())
                .andExpect(jsonPath("$.discountedProducts[1].id").doesNotExist());
    }
}