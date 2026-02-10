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

    @NotBlank
    private OrderStatus status;

    @NotBlank
    private BigDecimal total_amount;

    @NotBlank
    private User user;

    @NotBlank
    private List<OrderItem> orderItems = new ArrayList<>();

    public LocalDateTime getOrder_date() {
        return order_date;
    }

    public void setOrder_date(LocalDateTime order_date) {
        this.order_date = order_date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
