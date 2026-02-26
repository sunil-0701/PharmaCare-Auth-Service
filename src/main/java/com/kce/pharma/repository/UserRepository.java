package com.kce.pharma.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kce.pharma.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
}

