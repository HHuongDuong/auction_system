package com.example.auction.service;

import com.example.auction.config.JwtUtils;
import com.example.auction.model.Account;
import com.example.auction.model.Role;
import com.example.auction.model.RoleChange;
import com.example.auction.model.Status;
import com.example.auction.repository.AccountRepository;
import com.example.auction.repository.RoleChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RoleChangeRepository roleChangeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm tài khoản theo username
        Account account = accountRepository.findByAccountname(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Trả về đối tượng UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(account.getAccountname())
                .password(account.getPassword())
                .roles(account.getRole().name()) // Sử dụng tên role từ enum Role
                .build();
    }

    @Transactional
    public Account createAccount(String accountname, String name, String password, String email, Role role) {
        if (accountRepository.findByAccountname(accountname).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        Account account = new Account();
        account.setAccountname(accountname);
        account.setName(name);
        account.setPassword(passwordEncoder.encode(password));
        account.setEmail(email);
        account.setRole(role); // Gán role tương ứng

        return accountRepository.save(account);
    }

    public String login(String accountname, String password, AuthenticationManager authenticationManager) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(accountname, password)
            );

            // Đảm bảo rằng authentication đã thành công
            if (authentication.isAuthenticated()) {
                return jwtUtils.generateToken(accountname); // Trả về JWT nếu thành công
            } else {
                throw new RuntimeException("Invalid credentials");
            }
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    public Account getAccountInfo(String accountname) {
        return accountRepository.findByAccountname(accountname)
                .orElseThrow(() -> new RuntimeException("Account not found for username: " + accountname));
    }

    public RoleChange requestAdminRole(String accountname) {
        Optional<Account> accountOpt = accountRepository.findByAccountname(accountname);
        if (accountOpt.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        RoleChange request = new RoleChange();
        request.setAccountname(accountname);
        request.setRequestedRole(Role.ADMINISTRATOR);
        return roleChangeRepository.save(request);
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Transactional
    public RoleChange approveRequest(Long requestId) {
        RoleChange request = accountRepository.findRequestById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found."));
        if (request.getStatus() != Status.PENDING) {
            throw new RuntimeException("Request has already been processed.");
        }

        Account account = accountRepository.findByAccountname(request.getAccountname())
                .orElseThrow(() -> new RuntimeException("User not found."));
        account.setRole(request.getRequestedRole());
        accountRepository.save(account);

        request.setStatus(Status.APPROVED);
        return roleChangeRepository.save(request);
    }

    public List<RoleChange> getAllRequests() {
        return roleChangeRepository.findAll();
    }

    // Xóa tài khoản
    public void deleteAccount(Long id) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new RuntimeException("Account not found with ID: " + id);
        }
        accountRepository.deleteById(id);
    }

    // Lấy danh sách tài khoản
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Cập nhật quyền hạn tài khoản
    public Account updateAccountRole(Long id, Role role) {
        Optional<Account> accountOptional = accountRepository.findById(id);
        if (accountOptional.isEmpty()) {
            throw new RuntimeException("Account not found with ID: " + id);
        }

        Account account = accountOptional.get();
        account.setRole(role);
        return accountRepository.save(account);
    }
}
