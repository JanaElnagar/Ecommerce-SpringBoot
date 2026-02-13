package com.example.ecommercespringboot.repository;

import com.example.ecommercespringboot.entity.Order;
import com.example.ecommercespringboot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}
