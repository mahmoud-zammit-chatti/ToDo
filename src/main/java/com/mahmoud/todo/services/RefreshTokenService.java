package com.mahmoud.todo.services;


import com.mahmoud.todo.models.entities.AppUser;
import com.mahmoud.todo.models.entities.RefreshToken;
import com.mahmoud.todo.repos.RefreshTokenRepo;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;


@Service
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;
    public RefreshTokenService(RefreshTokenRepo refreshTokenRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
    }

    public RefreshToken generateRefreshToken(AppUser appUser){
        RefreshToken refreshToken=new RefreshToken();
        refreshToken.setUser(appUser);
        refreshToken.setRevoked(false);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(30, ChronoUnit.DAYS));

        return refreshTokenRepo.save(refreshToken);

    }

    public RefreshToken verifyRefreshToken(String token){
        RefreshToken refreshtoken= refreshTokenRepo.findByToken(token)
                .orElseThrow(()-> new AuthenticationCredentialsNotFoundException("Refresh token not found"));
        if(refreshtoken.isRevoked()){
            throw new AuthenticationCredentialsNotFoundException("Refresh token revoked");
        }
        if(refreshtoken.getExpiryDate().isBefore(Instant.now())){
            throw new AuthenticationCredentialsNotFoundException("Refresh token expired");
        }
        return refreshtoken;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken){

        oldToken.setRevoked(true);
        refreshTokenRepo.save(oldToken);
        return generateRefreshToken(oldToken.getUser());

    }

}

