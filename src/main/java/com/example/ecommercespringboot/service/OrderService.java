package com.example.ecommercespringboot.service;

import com.example.ecommercespringboot.dto.OrderCreateDto;
import com.example.ecommercespringboot.dto.OrderResponseDto;
import com.example.ecommercespringboot.entity.Order;
import com.example.ecommercespringboot.entity.OrderItem;
import com.example.ecommercespringboot.exception.ResourceNotFoundException;
import com.example.ecommercespringboot.mapper.OrderMapper;
import com.example.ecommercespringboot.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderMapper orderMapper;

	public OrderResponseDto createOrder(OrderCreateDto dto){
		Order order = orderMapper.toEntity(dto);
		orderRepository.save(order);
		return orderMapper.toDto(order);
	}

	public OrderResponseDto getOrder(Long id){
		Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
		return orderMapper.toDto(order);
	}

	public OrderResponseDto updateOrder(Long id, OrderCreateDto dto){
		Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
		order.setOrder_date(dto.getOrder_date());
		order.setStatus(dto.getStatus());
		order.setTotal_amount(dto.getTotal_amount());
		order.setUser(dto.getUser());

		List<OrderItem> newItems = dto.getOrderItems();
		order.getOrderItems().clear();
		if (newItems != null) {
			for (OrderItem item : newItems) {
				item.setOrder(order);
			}
			order.setOrderItems(newItems);
		}

		orderRepository.save(order);
		return orderMapper.toDto(order);
	}

	public void deleteOrder(Long id){
		Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
		orderRepository.delete(order);
	}
}
