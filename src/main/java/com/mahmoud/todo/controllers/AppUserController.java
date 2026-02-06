package com.mahmoud.todo.controllers;


import com.mahmoud.todo.DTOs.auth.RegisterAppUserDTO;
import com.mahmoud.todo.domain.user.AppUser;
import com.mahmoud.todo.services.user.AppUserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AppUserController {
    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

//
//    @PostMapping("/api/v1/auth/login")
//    public String login(@RequestBody @Valid LoginAppUserDTO loginAppUserDTO) {
//        return appUserService.verifyUser(loginAppUserDTO);
//    }
    @PostMapping("/api/v1/auth/register")
    public void register(@RequestBody @Valid RegisterAppUserDTO registerAppUserDTO) {
        appUserService.registerUser(registerAppUserDTO);
    }
    @GetMapping("/api/v1/admin/users")
    public ResponseEntity<List<AppUser>> getAllUsers(){
        return ResponseEntity.ok().body( appUserService.getAllUsers());
    }

    @GetMapping("/api/v1/me")
    public ResponseEntity<Map<String, Object>> me(Authentication auth) {
        return  ResponseEntity.ok().body( Map.of(
                "username", auth.getName(),
                "roles", auth.getAuthorities()

        ));
    }


}
