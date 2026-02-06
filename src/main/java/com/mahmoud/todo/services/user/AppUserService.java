package com.mahmoud.todo.services.user;


import com.mahmoud.todo.domain.exceptions.InvalidPasswordException;
import com.mahmoud.todo.domain.exceptions.UserAlreadyExistsException;
import com.mahmoud.todo.domain.user.AppUser;
import com.mahmoud.todo.DTOs.auth.RegisterAppUserDTO;
import com.mahmoud.todo.domain.user.Role;
import com.mahmoud.todo.repos.AppUserRepo;
import com.mahmoud.todo.security.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
