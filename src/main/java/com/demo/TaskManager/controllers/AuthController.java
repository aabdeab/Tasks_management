package com.demo.TaskManager.controllers;

import com.demo.TaskManager.common.ApiResponse;
import com.demo.TaskManager.dtos.LoginRequest;
import com.demo.TaskManager.dtos.RegisterRequest;
import com.demo.TaskManager.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid RegisterRequest dto) {
        String token;
        token = authService.registerUser(dto);
        ApiResponse<String> response = ApiResponse.success(token, "Inscription réussie");
        return ResponseEntity.ok(response) ;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        ApiResponse<String> response = ApiResponse.success(token, "Connexion réussie");
        return ResponseEntity.ok(response);
    }
}


