package com.ust_internal.productdiscountapi.mapper;

import com.ust_internal.productdiscountapi.dto.ProductDto;
import com.ust_internal.productdiscountapi.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    
    public ProductEntity toEntity(ProductDto productDto) {
        if (productDto == null) {
            return null;
        }
        
        ProductEntity entity = new ProductEntity();
        entity.setName(productDto.getName());
        entity.setCategory(productDto.getCategory());
        entity.setPrice(productDto.getPrice());
        entity.setQuantity(productDto.getQuantity() != null ? productDto.getQuantity() : 0);
        
        return entity;
    }
    
    public ProductDto toDto(ProductEntity productEntity) {
        if (productEntity == null) {
            return null;
        }
        
        return ProductDto.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .quantity(productEntity.getQuantity())
                .category(productEntity.getCategory())
                .build();
    }
}