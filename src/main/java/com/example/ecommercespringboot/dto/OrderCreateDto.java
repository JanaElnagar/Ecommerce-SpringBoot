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

    @NotBlank
    private LocalDateTime order_date;

    private String status;

    @NotBlank
    private BigDecimal total_amount;

    @NotBlank
    private List<OrderItemDto> orderItems = new ArrayList<>();

    public LocalDateTime getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDateTime order_date) {
        this.order_date = order_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }

    public List<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }
}
