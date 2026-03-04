package com.tcommerce.TCommerce.domain.repositories.implementations.auth;

import com.tcommerce.TCommerce.domain.entities.auth.PasswordResetToken;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.PasswordResetTokenRepository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    private final List<PasswordResetToken> tokens = new ArrayList<>();

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        if (token.getId() == null) {
            token.setId(UUID.randomUUID().toString());
        }
        tokens.removeIf(t -> t.getId().equals(token.getId()));
        tokens.add(token);
        return token;
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return tokens.stream()
                .filter(t -> t.getToken().equals(token))
                .findFirst();
    }

    @Override
    public Optional<PasswordResetToken> findByUser(User user) {
        return tokens.stream()
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .findFirst();
    }

    @Override
    public void delete(PasswordResetToken token) {
        tokens.removeIf(t -> t.getId().equals(token.getId()));
    }
}
