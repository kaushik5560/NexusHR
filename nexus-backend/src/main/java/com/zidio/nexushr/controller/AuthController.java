package com.zidio.nexushr.controller;

import com.zidio.nexushr.dto.JwtResponse;
import com.zidio.nexushr.dto.LoginRequest;
import com.zidio.nexushr.dto.MessageResponse;
import com.zidio.nexushr.dto.SignupRequest;
import com.zidio.nexushr.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST: /api/auth/signup - Register new employee
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            MessageResponse response = authService.registerEmployee(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("An error occurred during registration: " + e.getMessage()));
        }
    }

    // POST: /api/auth/login - Authenticate & return JWT token
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.authenticateEmployee(loginRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid email or password!"));
        }
    }
}