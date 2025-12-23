package com.mahmoud.todo.services;


import com.mahmoud.todo.exceptions.InvalidPasswordException;
import com.mahmoud.todo.exceptions.RessourceNotFoundException;
import com.mahmoud.todo.exceptions.UserAlreadyExistsException;
import com.mahmoud.todo.models.DTOs.LoginAppUserDTO;
import com.mahmoud.todo.models.entities.AppUser;
import com.mahmoud.todo.models.DTOs.RegisterAppUserDTO;
import com.mahmoud.todo.Role;
import com.mahmoud.todo.repos.AppUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.Authenticator;
import java.util.List;


@Service
public class AppUserService {
    private final AppUserRepo appUserRepo;


    private final AuthenticationManager authManager;
    private final JWTService jwtService ;


    public AppUserService(AppUserRepo appUserRepo, AuthenticationManager authManager, JWTService jwtService) {
        this.appUserRepo = appUserRepo;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }


    public void registerUser(RegisterAppUserDTO registerAppUserDTO ) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if(appUserRepo.existsByUsername(registerAppUserDTO.getUsername())){
            throw new UserAlreadyExistsException("Username already exists");
        }
        if(registerAppUserDTO.getPassword().toLowerCase().contains(registerAppUserDTO.getUsername().toLowerCase())){
            throw new InvalidPasswordException("Password cannot contain username");
        }

        AppUser user = new AppUser();
        user.setUsername(registerAppUserDTO.getUsername());
        user.setPassword(passwordEncoder.encode( registerAppUserDTO.getPassword()));
        user.setRole(Role.USER);
        if(registerAppUserDTO.getUsername().equals("hamouda")) user.setRole(Role.ADMIN);
        appUserRepo.save(user);
    }

//    public String verifyUser(LoginAppUserDTO loginAppUserDTO){
//        Authentication authentication =
//                authManager.authenticate(new UsernamePasswordAuthenticationToken(loginAppUserDTO.getUsername(),loginAppUserDTO.getPassword()));
//        if(authentication.isAuthenticated()){
//            return jwtService.generateToken(loginAppUserDTO.getUsername());
//        }else{
//            throw new RessourceNotFoundException("Invalid Credentials");
//
//        }
//    }

    public List<AppUser> getAllUsers() {
        return appUserRepo.findAll();
    }
}
