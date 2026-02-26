package com.kce.pharma.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kce.pharma.client.NotificationClient;
import com.kce.pharma.dto.RegisterRequest;
import com.kce.pharma.entity.Role;
import com.kce.pharma.entity.Status;
import com.kce.pharma.entity.User;
import com.kce.pharma.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final NotificationClient notificationClient;

    public UserService(UserRepository repository,
                       PasswordEncoder encoder,
                       NotificationClient notificationClient) {
        this.repository = repository;
        this.encoder = encoder;
        this.notificationClient = notificationClient;
    }

    
    public List<User> getAllUsers() {
        return repository.findAll();
    }
    public void initiatePasswordReset(String email) {

        Optional<User> optionalUser = repository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            return; 
        }

        User user = optionalUser.get();

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));

        repository.save(user);

        notificationClient.sendEmail(
            user.getEmail(),
            "Password Reset - PharmaCare",
            "Click the link below to reset your password:\n\n" +
            "http://localhost:5173/reset-password?token=" + token +
            "\n\nThis link expires in 15 minutes."
        );
    }
    
    public void createUser(RegisterRequest request) {

        if (repository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Generate temporary password
        String tempPassword = UUID.randomUUID()
                .toString()
                .substring(0, 8);

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(tempPassword));
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));
        user.setName(request.getName());
        user.setStatus(Status.Active);
        user.setJoinDate(LocalDate.now());
        user.setFirstLogin(true);
        user.setPhoneNumber(request.getPhoneNumber());

        repository.save(user);

        
        notificationClient.sendEmail(
                request.getEmail(),
                "Account Created - PharmaCare",
                "Hello " + request.getName() + ",\n\n" +
                "Your account has been created successfully.\n\n" +
                "Email: " + request.getEmail() + "\n" +
                "Temporary Password: " + tempPassword + "\n\n" +
                "Please change your password after your first login.\n\n" +
                "Regards,\nPharmaCare Management System"
        );
    }
    public void resetPassword(String token, String newPassword) {

        User user = repository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expired");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        user.setFirstLogin(false);

        repository.save(user);
    }
    
    public void updateStatus(String id, String status) {

        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(Status.valueOf(status));
        repository.save(user);
    }

    
    public void deleteUser(String id) {
        repository.deleteById(id);
    }

    
    public void changePassword(String email, String newPassword) {

        User user = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(newPassword));
        user.setFirstLogin(false);

        repository.save(user);
    }
}