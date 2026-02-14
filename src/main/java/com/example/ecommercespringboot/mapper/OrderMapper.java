package com.example.ecommercespringboot.mapper;

import com.example.ecommercespringboot.dto.OrderCreateDto;
import com.example.ecommercespringboot.dto.OrderResponseDto;
import com.example.ecommercespringboot.entity.Order;
import com.example.ecommercespringboot.entity.OrderItem;
import com.example.ecommercespringboot.entity.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public Order toEntity(OrderCreateDto dto){
        try {
            Order order = new Order();
            order.setOrder_date(dto.getOrder_date());
            order.setStatus(OrderStatus.valueOf(dto.getStatus()));
            order.setTotal_amount(dto.getTotal_amount());

//            List<OrderItem> items = dto.getOrderItems();
//            if (items != null) {
//                for (OrderItem item : items) {
//                    item.setOrder(order);
//                }
//                order.setOrderItems(items);
//            }
            return order;
        } catch (Exception e) {
            throw new RuntimeException(e);  // todo return suitable error
        }
    }

    public OrderResponseDto toDto(Order order){
        if (order == null) return null;
        return new OrderResponseDto(order.getId(), order.getOrder_date(), order.getStatus(), order.getTotal_amount());
    }
}
