package com.example.ecommercespringboot.service;

import com.example.ecommercespringboot.dto.UserDto;
import com.example.ecommercespringboot.entity.User;
import com.example.ecommercespringboot.mapper.UserMapper;
import com.example.ecommercespringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserMapper userMapper;


    public User register(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setPassword(encoder.encode(userDto.getPassword()));
        repo.save(user);
        return user;
    }
}
