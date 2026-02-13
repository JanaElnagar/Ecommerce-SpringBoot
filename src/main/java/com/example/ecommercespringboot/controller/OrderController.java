package com.example.ecommercespringboot.controller;

import com.example.ecommercespringboot.dto.OrderCreateDto;
import com.example.ecommercespringboot.dto.OrderResponseDto;
import com.example.ecommercespringboot.service.OrderService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PostMapping("/create")
	public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateDto dto){
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(dto));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{id}")
	public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id){
		return ResponseEntity.ok(orderService.getOrder(id));
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public ResponseEntity<List<OrderResponseDto>> getAllOrders(Authentication authentication){
		return ResponseEntity.ok(orderService.getAllOrders(authentication));
	}

	@PreAuthorize("isAuthenticated()")
	@PutMapping("/{id}")
	public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @RequestBody OrderCreateDto dto){
		return ResponseEntity.ok(orderService.updateOrder(id, dto));
	}

	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id){
		orderService.deleteOrder(id);
		return ResponseEntity.noContent().build();
	}
}
