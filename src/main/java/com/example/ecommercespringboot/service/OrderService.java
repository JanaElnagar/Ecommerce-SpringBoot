package com.example.ecommercespringboot.service;

import com.example.ecommercespringboot.dto.OrderCreateDto;
import com.example.ecommercespringboot.dto.OrderItemDto;
import com.example.ecommercespringboot.dto.OrderResponseDto;
import com.example.ecommercespringboot.entity.*;
import com.example.ecommercespringboot.exception.ResourceNotFoundException;
import com.example.ecommercespringboot.mapper.OrderMapper;
import com.example.ecommercespringboot.repository.OrderRepository;
import com.example.ecommercespringboot.repository.ProductRepository;
import com.example.ecommercespringboot.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private UserRepository userRepository;


	@Transactional
	public OrderResponseDto createOrder(OrderCreateDto dto, Authentication authentication){
		String username = authentication.getName();
		User user = userRepository.findByUsername(username);

		if (user == null) {
			throw new ResourceNotFoundException("User not found");
		}

		Order order = new Order();
		order.setOrder_date(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);
		order.setUser(user);

		List<OrderItem> orderItems = OrderItemsDtoToEntity(dto.getOrderItems(),order);
		order.setOrderItems(orderItems);
		order.setTotal_amount(getTotalAmount(orderItems));

		updateStockQuantity(orderItems);

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

	@Transactional
	public OrderResponseDto updateOrder(Long id, OrderCreateDto dto, Authentication authentication){
		Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));

		if (!isAdminOrOwner(authentication, order)) {
			throw new AccessDeniedException("You don't have permission to update this order");
		}

		restoreStockQuantity(order.getOrderItems());

		if (isAdmin(authentication)) {
			// Admin can change status (PENDING → SHIPPED)
			if (dto.getStatus() != null) {
				order.setStatus(OrderStatus.valueOf(dto.getStatus()));
			}
		}

		List<OrderItem> orderItems = order.getOrderItems();
		orderItems.clear();

		List<OrderItem> newItems = OrderItemsDtoToEntity(dto.getOrderItems(),order);
		orderItems.addAll(newItems);
		order.setOrderItems(orderItems);
		order.setTotal_amount(getTotalAmount(orderItems));

		updateStockQuantity(orderItems);

		orderRepository.save(order);
		return orderMapper.toDto(order);
	}

	@Transactional
	public void deleteOrder(Long id, Authentication authentication){
		Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order Not Found"));
		if (!isAdminOrOwner(authentication, order)) {
			throw new AccessDeniedException("You don't have permission to update this order");
		}
		restoreStockQuantity(order.getOrderItems());
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

	private List<OrderItem> OrderItemsDtoToEntity(List<OrderItemDto> itemDtos, Order order){
		List<OrderItem> orderItems = new ArrayList<>();
		if (itemDtos != null) {
			for (OrderItemDto itemDto : itemDtos) {
				Product product = productRepository.findById(itemDto.getProductId())
						.orElseThrow(() -> new ResourceNotFoundException("Product not found"));

				if (product.getStockQuantity() < itemDto.getQuantity()) {
					throw new IllegalStateException("Insufficient stock for product: " + product.getName());
				}

				OrderItem orderItem = new OrderItem();
				orderItem.setProduct(product);
				orderItem.setQuantity(itemDto.getQuantity());
				orderItem.setPrice_at_purchase(product.getPrice());
				orderItem.setOrder(order);
				orderItems.add(orderItem);

			}
		}
		return orderItems;
	}

	private BigDecimal getTotalAmount(List<OrderItem> orderItems){
		BigDecimal totalAmount = BigDecimal.ZERO;
		if (orderItems != null){
			for (OrderItem item : orderItems){
				totalAmount = totalAmount.add(item.getPrice_at_purchase().multiply(BigDecimal.valueOf(item.getQuantity())));
			}
		}
		return totalAmount;
	}

	private void updateStockQuantity(List<OrderItem> orderItems){
		if (orderItems != null){
			for (OrderItem item : orderItems){
				Product product = item.getProduct();
				product.setStock_quantity(product.getStockQuantity() - item.getQuantity());
				productRepository.save(product);
			}
		}
	}

	private void restoreStockQuantity(List<OrderItem> orderItems){
		if (orderItems != null){
			for (OrderItem item : orderItems){
				Product product = item.getProduct();
				product.setStock_quantity(product.getStockQuantity() + item.getQuantity());
				productRepository.save(product);
			}
		}
	}
}
