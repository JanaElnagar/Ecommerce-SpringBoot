package com.example.ecommercespringboot.mapper;

import com.example.ecommercespringboot.dto.OrderCreateDto;
import com.example.ecommercespringboot.dto.OrderResponseDto;
import com.example.ecommercespringboot.entity.Order;
import com.example.ecommercespringboot.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public Order toEntity(OrderCreateDto dto){
        Order order = new Order();
        order.setOrder_date(dto.getOrder_date());
        order.setStatus(dto.getStatus());
        order.setTotal_amount(dto.getTotal_amount());
        order.setUser(dto.getUser());

        List<OrderItem> items = dto.getOrderItems();
        if (items != null) {
            for (OrderItem item : items) {
                item.setOrder(order);
            }
            order.setOrderItems(items);
        }

        return order;
    }

    public OrderResponseDto toDto(Order order){
        if (order == null) return null;
        return new OrderResponseDto(order.getId(), order.getOrder_date(), order.getStatus(), order.getTotal_amount());
    }
}
