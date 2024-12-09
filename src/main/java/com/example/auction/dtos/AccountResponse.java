package com.example.auction.dtos;

import com.example.auction.model.Role;

public class AccountResponse {
    private String accountname;
    private String name;
    private String password;
    private String email;
    private Role role;

    public AccountResponse(String accountname, String name, String password, String email, Role role) {
        this.accountname = accountname;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
