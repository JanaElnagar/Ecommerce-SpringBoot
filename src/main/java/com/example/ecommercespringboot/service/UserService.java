package com.example.ecommercespringboot.service;

import com.example.ecommercespringboot.dto.RoleDto;
import com.example.ecommercespringboot.dto.UserDto;
import com.example.ecommercespringboot.entity.MyUserDetails;
import com.example.ecommercespringboot.entity.Role;
import com.example.ecommercespringboot.entity.User;
import com.example.ecommercespringboot.mapper.UserMapper;
import com.example.ecommercespringboot.repository.RoleRepository;
import com.example.ecommercespringboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository rolesRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    private UserMapper userMapper;


    public User register(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user.setPassword(encoder.encode(userDto.getPassword()));
        Set<Role> roles = userDto.getRoles().stream().map(roleDTO -> {
            //Single role
            Role role = getOrCreateRole(roleDTO);
            return role;
        }).collect(Collectors.toSet());
        user.setRoles(roles);
        userRepository.save(user);
        return user;
    }

    private Role getOrCreateRole(RoleDto roleDto) {
        return rolesRepository.findByName(roleDto.getName()).orElseGet(() -> {
            Role role = new Role();
            role.setName(roleDto.getName());
            return rolesRepository.save(role);
        });
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }

        return new MyUserDetails(user);
    }
}