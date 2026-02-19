package com.tcommerce.TCommerce.infrastructure.persistence.mappers.auth;

import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toDomain(UserEntity user){
        if (user == null) return null;
        return new User(
                user.getId(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserEntity toEntity(User user) {
        if (user == null) return null;
        UserEntity entity = new UserEntity(
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
        entity.setId(user.getId());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }
}
