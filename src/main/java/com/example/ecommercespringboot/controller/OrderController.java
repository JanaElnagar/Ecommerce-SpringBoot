package com.example.ecommercespringboot.controller;

import com.example.ecommercespringboot.dto.OrderCreateDto;
import com.example.ecommercespringboot.dto.OrderResponseDto;
import com.example.ecommercespringboot.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@PostMapping("/create")
	public ResponseEntity<OrderResponseDto> createOrder(@RequestBody OrderCreateDto dto){
		return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(dto));
	}

	@GetMapping("/{id}")
	public ResponseEntity<OrderResponseDto> getOrder(@PathVariable Long id){
		return ResponseEntity.ok(orderService.getOrder(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @RequestBody OrderCreateDto dto){
		return ResponseEntity.ok(orderService.updateOrder(id, dto));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long id){
		orderService.deleteOrder(id);
		return ResponseEntity.noContent().build();
	}
}
