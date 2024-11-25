package com.example.auction.repository;

import com.example.auction.model.Account;
import com.example.auction.model.Role;
import com.example.auction.model.RoleChange;
import com.example.auction.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountname(String accountName);

    Optional<Account> findByRole(Role role);

    @Query("SELECT r FROM RoleChange r WHERE r.status = :status")
    Optional<RoleChange> findRequestsByStatus(@Param("status") Status status);

    @Query("SELECT r FROM RoleChange r WHERE r.id = :id")
    Optional<RoleChange> findRequestById(@Param("id") Long id);
}
