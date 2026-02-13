package com.example.ecommercespringboot.mapper;

import com.example.ecommercespringboot.dto.UserDto;
import com.example.ecommercespringboot.entity.Role;
import com.example.ecommercespringboot.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toEntity(UserDto dto){
        if (dto == null){
            return null;
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setFirst_name(dto.getFirst_name());
        user.setLast_name(dto.getLast_name());
        user.setUsername(dto.getUsername());

        // skip password -> will be encrypted first and saved in controller
        return user;
    }

    public UserDto toDto(User user){
        if (user == null){
            return null;
        }
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        dto.setFirst_name(user.getFirst_name());
        dto.setLast_name(user.getLast_name());
        dto.setUsername(user.getUsername());
        // skip password
        return dto;
    }



}
