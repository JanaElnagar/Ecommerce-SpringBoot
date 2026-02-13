package com.example.ecommercespringboot.service;

import com.example.ecommercespringboot.dto.OrderCreateDto;
import com.example.ecommercespringboot.dto.OrderResponseDto;
import com.example.ecommercespringboot.entity.Order;
import com.example.ecommercespringboot.entity.OrderItem;
import com.example.ecommercespringboot.entity.User;
import com.example.ecommercespringboot.exception.ResourceNotFoundException;
import com.example.ecommercespringboot.mapper.OrderMapper;
import com.example.ecommercespringboot.repository.OrderRepository;
import com.example.ecommercespringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private UserRepository userRepository;


	public OrderResponseDto createOrder(OrderCreateDto dto, Authentication authentication){
		String username = authentication.getName();
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new ResourceNotFoundException("User not found");
		}

		Order order = orderMapper.toEntity(dto);
		order.setUser(user);
		orderRepository.save(order);
		return orderMapper.toDto(order);
	}

	public OrderResponseDto getOrder(Long id, Authentication authentication){
		Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

		if (!isAdminOrOwner(authentication, order)) {
			throw new AccessDeniedException("You don't have permission to view this order");
		}

		return orderMapper.toDto(order);
	}

	public List<OrderResponseDto> getAllOrders(Authentication authentication){
		List<Order> orders;

		// If ADMIN, return all orders. If USER, return only their orders
		if (isAdmin(authentication)) {
			orders = orderRepository.findAll();
		} else {
			String username = authentication.getName();
			User user = userRepository.findByUsername(username);
			orders = orderRepository.findByUser(user);
		}

		return orders.stream()
				.map(orderMapper::toDto)
				.collect(Collectors.toList());
	}

	public OrderResponseDto updateOrder(Long id, OrderCreateDto dto, Authentication authentication){
		Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

		if (!isAdminOrOwner(authentication, order)) {
			throw new AccessDeniedException("You don't have permission to update this order");
		}

		order.setOrder_date(dto.getOrder_date());
		order.setStatus(dto.getStatus());
		order.setTotal_amount(dto.getTotal_amount());

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

	public void deleteOrder(Long id, Authentication authentication){
		Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
		if (!isAdminOrOwner(authentication, order)) {
			throw new AccessDeniedException("You don't have permission to update this order");
		}
		orderRepository.delete(order);
	}


	private boolean isAdmin(Authentication authentication) {
		return authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}

	private boolean isAdminOrOwner(Authentication authentication, Order order) {
		if (isAdmin(authentication)) {
			return true;
		}

		String username = authentication.getName();
		return order.getUser().getUsername().equals(username);
	}
}
