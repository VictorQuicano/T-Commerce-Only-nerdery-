package com.tcommerce.TCommerce.domain.entities.commerce;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.Optional;

public class Product implements BaseEntity {
    private String id;
    private String name;
    private String description;
    private Optional<LocalDateTime> deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product(String id, String name, String description, Optional<LocalDateTime> deletedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Optional<LocalDateTime> getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Optional<LocalDateTime> deletedAt) {
        this.deletedAt = deletedAt;
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
