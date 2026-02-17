package com.tcommerce.TCommerce.domain.entities.commerce;


import com.tcommerce.TCommerce.domain.entities.BaseEntity;

import java.time.LocalDateTime;

public class Category implements BaseEntity {
    private String name;
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Category(String name, String id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
