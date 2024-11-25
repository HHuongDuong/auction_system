package com.example.auction.repository;

import com.example.auction.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAccountname(String accountname);

    @Query("SELECT p FROM Payment p JOIN Auction a ON a.id = :auctionId WHERE a.endTime > :startTime AND a.endTime < :endTime")
    List<Payment> findPaymentsNeedingConfirmation(@Param("auctionId") Long auctionId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
}
