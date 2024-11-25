package com.example.auction.controller;

import com.example.auction.config.JwtUtils;
import com.example.auction.dtos.LoginRequest;
import com.example.auction.dtos.LoginResponse;
import com.example.auction.dtos.RegisterRequest;
import com.example.auction.model.Account;
import com.example.auction.model.Role;
import com.example.auction.model.RoleChange;
import com.example.auction.repository.AccountRepository;
import com.example.auction.service.AccountService;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AccountController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountService accountService;
    private Log log;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = accountService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerAccount(@RequestBody RegisterRequest registerRequest) {
        Account account = accountService.createAccount(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                Role.USER // Mặc định gán role là USER
        );
        return ResponseEntity.ok("Account registered successfully: " + account.getAccountname());
    }

    @PostMapping("/request-admin")
    public ResponseEntity<?> requestAdminRole(@RequestParam String username) {
        accountService.requestAdminRole(username);
        return ResponseEntity.ok("Role updated to ADMINISTRATOR and logged successfully for user: " + username);
    }

    @GetMapping("/role-change-requests")
    public ResponseEntity<?> getAllRoleChangeRequests() {
        List<RoleChange> requests = accountService.getAllRequests();
        return ResponseEntity.ok(requests);
    }
}
