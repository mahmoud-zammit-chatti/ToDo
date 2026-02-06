package com.mahmoud.todo.repos;

import com.mahmoud.todo.domain.user.AppUser;
import com.mahmoud.todo.domain.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepo extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(AppUser user);
}
