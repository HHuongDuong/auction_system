package com.example.auction.controller;

import com.example.auction.model.Auction;
import com.example.auction.model.Bid;
import com.example.auction.service.AuctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @GetMapping("/active")
    public List<Auction> getAllActiveAuctions() {
        return auctionService.getAllActiveAuctions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable Long id) {
        Auction auction = auctionService.findById(id);
        return auction != null ? ResponseEntity.ok(auction) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public Auction createAuction(@RequestBody Auction auction) {
        return auctionService.createAuction(auction);
    }

    @PostMapping("/{auctionId}/bid")
    public Auction placeBid(
            @PathVariable Long auctionId,
            @RequestParam String bidder,
            @RequestParam BigDecimal bidAmount) {
        return auctionService.placeBid(auctionId, bidder, bidAmount);
    }

    @GetMapping("/{auctionId}/bids")
    public List<Bid> getBidsForAuction(@PathVariable Long auctionId) {
        return auctionService.getBidsForAuction(auctionId);
    }
}
