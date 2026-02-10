package com.example.ecommercespringboot.mapper;

import com.example.ecommercespringboot.dto.ProductCreateDto;
import com.example.ecommercespringboot.dto.ProductResponseDto;
import com.example.ecommercespringboot.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductCreateDto dto){
        Product product =  new Product(dto.getName(), dto.getDescription(), dto.getPrice(), dto.getStockQuantity());
        return product;
    }

    public ProductResponseDto toDto(Product product){
        ProductResponseDto productResponseDto =  new ProductResponseDto(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStockQuantity());
        return productResponseDto;
    }
}
