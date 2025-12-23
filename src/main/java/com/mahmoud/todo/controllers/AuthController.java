package com.mahmoud.todo.controllers;


import com.mahmoud.todo.models.DTOs.LoginAppUserDTO;
import com.mahmoud.todo.models.Responses.AuthResponse;
import com.mahmoud.todo.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;


    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginAppUserDTO user){
        return ResponseEntity.ok().body(authService.login(user));

    }

}
