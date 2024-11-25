package com.example.auction.authentication;

import com.example.auction.model.Account;
import com.example.auction.model.Role;
import com.example.auction.repository.AccountRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.auction.config.SecurityConfig;

@Component
public class AdminInitializer {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createDefaultAdmin() {
        // Kiểm tra nếu không có Admin nào tồn tại
        if (accountRepository.findByRole(Role.ADMINISTRATOR).isEmpty()) {
            Account defaultAdmin = new Account();
            defaultAdmin.setAccountname("admin");
            defaultAdmin.setName("Đỗ Ánh Dương");
            String rawPassword = "admin123";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            defaultAdmin.setPassword(encodedPassword);
            defaultAdmin.setEmail("admin@example.com");
            defaultAdmin.setRole(Role.ADMINISTRATOR);
            accountRepository.save(defaultAdmin);

        }
    }
}

