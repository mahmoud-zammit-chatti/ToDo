package com.mahmoud.todo.services;

import com.mahmoud.todo.models.DTOs.LoginAppUserDTO;
import com.mahmoud.todo.models.Responses.AuthResponse;
import com.mahmoud.todo.models.entities.AppUser;
import com.mahmoud.todo.repos.AppUserRepo;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
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

    public AuthService(AuthenticationManager authManager, JWTService jwtService,  AppUserRepo appUserRepo, RefreshTokenService refreshTokenService) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.appUserRepo = appUserRepo;
        this.refreshTokenService = refreshTokenService;
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

}
