package com.example.ecommercespringboot.repository;

import com.example.ecommercespringboot.entity.Role;
import com.example.ecommercespringboot.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
