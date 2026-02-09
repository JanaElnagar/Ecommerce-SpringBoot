package com.example.ecommercespringboot.controller;

import com.example.ecommercespringboot.dto.UserDto;
import com.example.ecommercespringboot.entity.User;
import com.example.ecommercespringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDto dto){
        try {
            User user = userService.register(dto);
            return ResponseEntity.ok("User registered successfully: " + user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
}
