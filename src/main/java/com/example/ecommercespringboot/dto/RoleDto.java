package com.example.ecommercespringboot.dto;

import com.example.ecommercespringboot.entity.RoleName;

public class RoleDto {
    private RoleName name;


    public RoleDto() {
    }

    public RoleDto(RoleName name) {
        this.name = name;
    }

    public RoleName getName() {
        return name;
    }

    public void setName(RoleName name) {
        this.name = name;
    }
}
