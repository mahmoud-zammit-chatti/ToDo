package com.mahmoud.todo.controllers;


import com.mahmoud.todo.models.DTOs.LoginAppUserDTO;
import com.mahmoud.todo.models.DTOs.RegisterAppUserDTO;
import com.mahmoud.todo.models.entities.AppUser;
import com.mahmoud.todo.services.AppUserService;
import jakarta.validation.Valid;
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
    public List<AppUser> getAllUsers(){
        return appUserService.getAllUsers();
    }

    @GetMapping("/api/v1/me")
    public Map<String, Object> me(Authentication auth) {
        return Map.of(
                "username", auth.getName(),
                "roles", auth.getAuthorities()

        );
    }


}
