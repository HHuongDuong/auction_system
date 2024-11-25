package com.example.auction.service;

import com.example.auction.config.JwtUtils;
import com.example.auction.model.Account;
import com.example.auction.model.Role;
import com.example.auction.model.RoleChange;
import com.example.auction.model.Status;
import com.example.auction.repository.AccountRepository;
import com.example.auction.repository.RoleChangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private static AccountRepository accountRepository;

    @Autowired
    private RoleChangeRepository roleChangeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        AccountService.accountRepository = accountRepository;
    }

    public Account createAccount(String accountname, String password, String email, Role role) {
        if (accountRepository.findByAccountname(accountname).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }

        Account account = new Account();
        account.setAccountname(accountname);
        account.setPassword(passwordEncoder.encode(password));
        account.setEmail(email);
        account.setRole(role != null ? role : Role.USER); // Gán role tương ứng
        return accountRepository.save(account);
    }

    public String login(String accountName, String password){
        Optional<Account> accountOptional = accountRepository.findByAccountname(accountName);
        if (accountOptional.isEmpty()){
            throw new RuntimeException("Invalid username or password!");
        }

        Account account = accountOptional.get();

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new RuntimeException("Invalid username or password!");
        }

        return jwtUtils.generateToken(accountName);
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
}
