package com.mahmoud.todo.DTOs.urlcheck;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlCheckRequest {
    @NotBlank(message = "URL is required")
    private String url;
}
