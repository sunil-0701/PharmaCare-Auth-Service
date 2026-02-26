package com.kce.pharma.controller;
import com.kce.pharma.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.kce.pharma.entity.Status;
import com.kce.pharma.entity.User;
import com.kce.pharma.repository.UserRepository;
import com.kce.pharma.security.JwtUtil;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(UserRepository repository,
                          PasswordEncoder encoder,
                          JwtUtil jwtUtil,UserService userService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.userService=userService;
    }
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {

        String token = request.get("token");
        String newPassword = request.get("newPassword");

        userService.resetPassword(token, newPassword);

        return ResponseEntity.ok(
            Map.of("message", "Password reset successfully")
        );
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        userService.initiatePasswordReset(email);

        return ResponseEntity.ok(
            Map.of("message", "If this email exists, a reset link has been sent.")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String password = request.get("password");

        if (email == null || password == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Email and password are required"));
        }

        Optional<User> userOptional = repository.findByEmail(email.trim().toLowerCase());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        User user = userOptional.get();

        if (!encoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }

        if (user.getStatus() == Status.Inactive) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Account inactive"));
        }

        
        if (Boolean.TRUE.equals(user.isFirstLogin())) {
            return ResponseEntity.ok(Map.of(
                    "firstLogin", true,
                    "email", user.getEmail()
            ));
        }

        
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", user.getRole().name(),
                "name", user.getName(),
                "email", user.getEmail(),
                "firstLogin", false
        ));
    }
    @GetMapping("/me")
    public ResponseEntity<?> me(org.springframework.security.core.Authentication auth) {
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(Map.of("username", auth.getName()));
    }
}
