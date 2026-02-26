package com.kce.pharma;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
@EnableFeignClients
@SpringBootApplication
public class PharmaAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PharmaAuthServiceApplication.class, args);
    }

   
    }

