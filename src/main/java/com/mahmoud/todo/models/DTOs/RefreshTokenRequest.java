package com.mahmoud.todo.models.DTOs;


import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }


}

