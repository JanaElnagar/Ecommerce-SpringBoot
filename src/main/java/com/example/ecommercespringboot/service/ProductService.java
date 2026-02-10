package com.example.ecommercespringboot.service;

import com.example.ecommercespringboot.dto.ProductCreateDto;
import com.example.ecommercespringboot.dto.ProductResponseDto;
import com.example.ecommercespringboot.entity.Product;
import com.example.ecommercespringboot.exception.ResourceNotFoundException;
import com.example.ecommercespringboot.mapper.ProductMapper;
import com.example.ecommercespringboot.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ProductMapper productMapper;

    public ProductResponseDto createProduct(ProductCreateDto dto){
        Product product = productMapper.toEntity(dto);
        productRepo.save(product);

        return productMapper.toDto(product);
    }

    public ProductResponseDto getProduct(Long id){
        return productMapper.toDto(productRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product Not Found")));
    }

    public ProductResponseDto updateProduct(Long id, ProductCreateDto dto){
        Product product = productRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStock_quantity(dto.getStockQuantity());
        productRepo.save(product);
        return productMapper.toDto(product);
    }

    public void deleteProduct(Long id){
        Product product = productRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product Not Found"));
        productRepo.delete(product);
    }
}

