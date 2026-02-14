package com.mahmoud.todo.DTOs.urlcheck;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UrlCheckResponse {
    private String status; // "SAFE" or "MALICIOUS"
    private int riskScore;
    private List<String> sources;
    private Boolean redirectToGame;
}
