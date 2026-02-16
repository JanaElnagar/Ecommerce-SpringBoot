package com.example.ecommercespringboot.dto;

import com.example.ecommercespringboot.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderResponseDto {

	private Long id;
	private LocalDateTime order_date;
	private OrderStatus status;
	private BigDecimal total_amount;
	private Long user_id;
	private String username;

	public OrderResponseDto() {
	}

	public OrderResponseDto(Long id, LocalDateTime order_date, OrderStatus status, BigDecimal total_amount, Long user_id, String username) {
		this.id = id;
		this.order_date = order_date;
		this.status = status;
		this.total_amount = total_amount;
		this.user_id = user_id;
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
