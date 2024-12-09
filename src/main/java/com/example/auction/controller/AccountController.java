package com.example.auction.controller;

import com.example.auction.dtos.AccountRequest;
import com.example.auction.dtos.AccountResponse;
import com.example.auction.dtos.LoginRequest;
import com.example.auction.model.Account;
import com.example.auction.model.Role;
import com.example.auction.model.RoleChange;
import com.example.auction.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AccountController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AccountService accountService;

    // Xử lý đăng ký
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> processRegister(
            @RequestBody AccountRequest accountRequest) {

        Map<String, String> response = new HashMap<>();
        try {
            Account account = accountService.createAccount(
                    accountRequest.getAccountname(),
                    accountRequest.getName(),
                    accountRequest.getPassword(),
                    accountRequest.getEmail(),
                    accountRequest.getRole()
            );
            response.put("status", "success");
            response.put("message", "Registration successful for user: " + account.getAccountname());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/register")
    public ResponseEntity<Resource> showRegisterPage() {
        try {
            ClassPathResource resource = new ClassPathResource("static/register.html");
            return ResponseEntity.ok()
                    .header("Content-Type", "text/html")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Xử lý đăng nhập
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> processLogin(
            @RequestBody LoginRequest loginRequest) {

        Map<String, String> response = new HashMap<>();
        // Kiểm tra thông tin đăng nhập
        try {
            System.out.println("Login attempt: " + loginRequest.getAccountname());
            String token = accountService.login(loginRequest.getAccountname(), loginRequest.getPassword(), authenticationManager);
            response.put("status", "success");
            response.put("message", "Login successful!");
            response.put("token", token); // Trả về JWT token
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<Resource> showLoginPage() throws IOException {
        // Tạo resource từ tài nguyên tĩnh
        ClassPathResource resource = new ClassPathResource("static/login.html");

        // Trả về tài nguyên HTML dưới dạng ResponseEntity
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(resource);
    }


    // Lấy thông tin tài khoản
    @GetMapping("/{accountname}")
    public ResponseEntity<?> getAccountInfo(@PathVariable String accountname) {
        try {
            Account account = accountService.getAccountInfo(accountname);
            AccountResponse accountResponse = new AccountResponse(
                    account.getAccountname(),
                    account.getName(),
                    account.getPassword(),
                    account.getEmail(),
                    account.getRole());
            return ResponseEntity.ok(accountResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Yêu cầu cấp quyền admin
    @PostMapping("/request-admin")
    public ResponseEntity<?> requestAdminRole(@RequestParam String accountname) {
        try {
            RoleChange roleChange = accountService.requestAdminRole(accountname);
            return ResponseEntity.ok("Role change request submitted successfully for user: " + roleChange.getAccountname());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Lấy danh sách yêu cầu thay đổi quyền
    @GetMapping("/role-change-requests")
    public ResponseEntity<List<RoleChange>> getAllRoleChangeRequests() {
        List<RoleChange> requests = accountService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    // Phê duyệt yêu cầu thay đổi quyền
    @PostMapping("/approve-request/{requestId}")
    public ResponseEntity<?> approveRequest(@PathVariable Long requestId) {
        try {
            RoleChange approvedRequest = accountService.approveRequest(requestId);
            return ResponseEntity.ok("Request approved for user: " + approvedRequest.getAccountname());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Xóa tài khoản
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable Long id) {
        try {
            accountService.deleteAccount(id);
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Lấy danh sách tài khoản
    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    // Cập nhật quyền hạn tài khoản
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateAccountRole(
            @PathVariable Long id,
            @RequestParam Role  role) {
        try {
            Account account = accountService.updateAccountRole(id, role);
            return ResponseEntity.ok("Account role updated to: " + account.getRole());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
