package com.tcommerce.TCommerce.infrastructure.persistence.entities.auth;

import com.tcommerce.TCommerce.infrastructure.persistence.entities.BaseEntity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
    uniqueConstraints = @UniqueConstraint(name = "uq_user_email", columnNames = "email"))
public class UserEntity extends BaseEntity {
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;
    
    @Column(name = "email", nullable = false, length = 100)
    private String email;
    
    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ERole role = ERole.CLIENT;
}
