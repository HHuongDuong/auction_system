package com.example.auction.repository;

import com.example.auction.model.Bid;
import com.example.auction.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long>{
    List<Bid> findByAuction_AucId(Long auctionId);
}
