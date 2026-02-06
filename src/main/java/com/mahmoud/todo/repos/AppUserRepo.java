package com.mahmoud.todo.repos;

import com.mahmoud.todo.domain.user.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser, String> {
    boolean existsByUsername(String username);

    @Override
    Optional<AppUser> findById(String s);

    Optional<AppUser> findByUsername(String username);
}
