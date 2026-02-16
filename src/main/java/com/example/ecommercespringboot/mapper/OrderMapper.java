package com.example.ecommercespringboot.mapper;

import com.example.ecommercespringboot.dto.OrderCreateDto;
import com.example.ecommercespringboot.dto.OrderResponseDto;
import com.example.ecommercespringboot.entity.Order;
import com.example.ecommercespringboot.entity.OrderItem;
import com.example.ecommercespringboot.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderMapper {

    public OrderResponseDto toDto(Order order){
        if (order == null) return null;
        return new OrderResponseDto(order.getId(), order.getOrder_date(), order.getStatus(), order.getTotal_amount(), order.getUser().getId(), order.getUser().getUsername());
    }
}
