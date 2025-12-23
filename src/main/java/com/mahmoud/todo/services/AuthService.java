package com.mahmoud.todo.services;

import com.mahmoud.todo.models.DTOs.LoginAppUserDTO;
import com.mahmoud.todo.models.DTOs.RefreshTokenRequest;
import com.mahmoud.todo.models.Responses.AuthResponse;
import com.mahmoud.todo.models.entities.AppUser;
import com.mahmoud.todo.models.entities.RefreshToken;
import com.mahmoud.todo.repos.AppUserRepo;
import com.mahmoud.todo.repos.RefreshTokenRepo;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;




@Service
public class AuthService {
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final AppUserRepo appUserRepo;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepo refreshTokenRepo;

    public AuthService(AuthenticationManager authManager, JWTService jwtService, AppUserRepo appUserRepo, RefreshTokenService refreshTokenService, RefreshTokenRepo refreshTokenRepo) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.appUserRepo = appUserRepo;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepo = refreshTokenRepo;
    }

    @Transactional
    public  AuthResponse login(LoginAppUserDTO user){
        Authentication auth = authManager.authenticate( new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        AppUser appuser = appUserRepo.findByUsername(user.getUsername())
                .orElseThrow(()->new BadCredentialsException("User not found with username: "+user.getUsername()));
        String accessToken = jwtService.generateToken(appuser.getUsername());
        String refreshToken = refreshTokenService.generateRefreshToken(appuser).getToken();
        return new AuthResponse(accessToken,refreshToken);
    }


    @Transactional
    public AuthResponse refresh(String token) {

        RefreshToken refreshtoken= refreshTokenRepo.findByToken(token)
                .orElseThrow(()-> new AuthenticationCredentialsNotFoundException("Refresh token not found"));

        if(refreshtoken.isRevoked()){
            throw new AuthenticationCredentialsNotFoundException("Refresh token revoked");
        }
        if(refreshtoken.getExpiryDate().isBefore(java.time.Instant.now())){
            throw new AuthenticationCredentialsNotFoundException("Refresh token expired");
        }
        refreshtoken.setRevoked(true);


        String accessToken = jwtService.generateToken(refreshtoken.getUser().getUsername());
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshtoken);
        refreshTokenRepo.save(newRefreshToken);
        return new AuthResponse(accessToken,newRefreshToken.getToken());

    }

    public void logout(RefreshTokenRequest refreshTokenRequest) {

            RefreshToken refreshtoken= refreshTokenRepo.findByToken(refreshTokenRequest.getRefreshToken())
                    .orElse(null);
            if(refreshtoken!=null){
                refreshtoken.setRevoked(true);
            }

    }
}
