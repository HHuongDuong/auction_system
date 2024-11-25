package com.example.auction.repository;

import com.example.auction.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, Long>{
    List<Auction> findByEndTimeAfter(LocalDateTime now);
    List<Auction> findByIsExpired(boolean isExpired);

    Optional<Auction> findById(Long id);
}


