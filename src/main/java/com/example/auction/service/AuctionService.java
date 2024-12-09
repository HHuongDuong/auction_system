package com.example.auction.service;

import com.example.auction.model.Auction;
import com.example.auction.model.Bid;
import com.example.auction.repository.AuctionRepository;
import com.example.auction.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    public List<Auction> getAllActiveAuctions() {
        return auctionRepository.findByEndTimeAfter(LocalDateTime.now());
    }

    public Auction findById(Long id) {
        return auctionRepository.findById(id).orElse(null);
    }

    public Auction createAuction(Auction auction) {
        auction.setExpired(false);
        return auctionRepository.save(auction);
    }

    public Auction placeBid(Long auctionId, String bidder, BigDecimal bidAmount) {
        Optional<Auction> auctionOptional = auctionRepository.findById(auctionId);
        if (auctionOptional.isEmpty()) {
            throw new RuntimeException("Auction not found!");
        }

        Auction auction = auctionOptional.get();

        if (auction.isExpired() || auction.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Auction has ended!");
        }

        if (bidAmount.compareTo(auction.getHighestBid()) < 1) {
            throw new RuntimeException("Bid amount must be higher than the current highest bid!");
        }

        // Update auction with new highest bid
        auction.setHighestBid(bidAmount);
        auction.setHighestBidder(bidder);
        auctionRepository.save(auction);

        // Save the bid
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setBidAmount(bidAmount);
        bid.setBidTime(LocalDateTime.now());
        bidRepository.save(bid);

        return auction;
    }

    public List<Bid> getBidsForAuction(Long auctionId) {
        return bidRepository.findByAuction_AucId(auctionId);
    }
}

