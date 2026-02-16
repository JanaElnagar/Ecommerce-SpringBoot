package com.example.ecommercespringboot.dto;

import com.example.ecommercespringboot.entity.OrderItem;
import com.example.ecommercespringboot.entity.OrderStatus;
import com.example.ecommercespringboot.entity.User;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderCreateDto {

    private String status;

    @NotBlank
    private List<OrderItemDto> orderItems = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }
}
