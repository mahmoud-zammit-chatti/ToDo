package com.mahmoud.todo.controllers;

import com.mahmoud.todo.DTOs.urlcheck.UrlCheckRequest;
import com.mahmoud.todo.DTOs.urlcheck.UrlCheckResponse;
import com.mahmoud.todo.services.urlcheck.DetectionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*") // CAUTION: Configured for development. Restrict origins in production!
public class UrlCheckController {

    private final DetectionService detectionService;

    public UrlCheckController(DetectionService detectionService) {
        this.detectionService = detectionService;
    }

    @PostMapping("/check-url")
    public ResponseEntity<UrlCheckResponse> checkUrl(@Valid @RequestBody UrlCheckRequest request) {
        UrlCheckResponse response = detectionService.checkUrl(request.getUrl());
        return ResponseEntity.ok(response);
    }
}
