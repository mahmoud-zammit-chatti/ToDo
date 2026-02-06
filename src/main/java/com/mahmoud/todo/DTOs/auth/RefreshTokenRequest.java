package com.mahmoud.todo.DTOs.auth;


import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }


}

