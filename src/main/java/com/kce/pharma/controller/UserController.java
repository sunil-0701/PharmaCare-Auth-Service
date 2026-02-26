package com.kce.pharma.controller;

import com.kce.pharma.dto.RegisterRequest;
import com.kce.pharma.entity.User;
import com.kce.pharma.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || newPassword == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email and new password are required"));
        }

        userService.changePassword(email, newPassword);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
     
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
        userService.createUser(request);
        return ResponseEntity.ok("User created successfully");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {

        userService.updateStatus(id, status);
        return ResponseEntity.ok("Status updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
