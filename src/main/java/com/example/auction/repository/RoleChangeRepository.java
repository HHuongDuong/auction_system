package com.example.auction.repository;

import com.example.auction.model.RoleChange;
import com.example.auction.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleChangeRepository extends JpaRepository<RoleChange, Long> {
    Optional<RoleChange> findByStatus(Status status);
}
