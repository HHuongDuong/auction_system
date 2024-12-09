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

    // Lấy danh sách tất cả đấu giá đang hoạt động
    @GetMapping("/active")
    public List<Auction> getAllActiveAuctions() {
        return auctionService.getAllActiveAuctions();
    }

    // Xem chi tiết một đấu giá theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Auction> getAuctionById(@PathVariable Long id) {
        Auction auction = auctionService.findById(id);
        return auction != null ? ResponseEntity.ok(auction) : ResponseEntity.notFound().build();
    }

    // Tạo đấu giá mới
    @PostMapping
    public Auction createAuction(@RequestBody Auction auction) {
        return auctionService.createAuction(auction);
    }

    // Chỉnh sửa thông tin đấu giá
    @PutMapping("/{id}")
    public ResponseEntity<Auction> updateAuction(@PathVariable Long id, @RequestBody Auction updatedAuction) {
        try {
            Auction auction = auctionService.updateAuction(id, updatedAuction);
            return ResponseEntity.ok(auction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Xóa một đấu giá
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAuction(@PathVariable Long id) {
        try {
            auctionService.deleteAuction(id);
            return ResponseEntity.ok("Auction deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Đặt giá thầu trên đấu giá
    @PostMapping("/{auctionId}/bid")
    public ResponseEntity<Auction> placeBid(
            @PathVariable Long auctionId,
            @RequestParam String bidder,
            @RequestParam BigDecimal bidAmount) {
        try {
            Auction auction = auctionService.placeBid(auctionId, bidder, bidAmount);
            return ResponseEntity.ok(auction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Lấy danh sách các giá thầu của một đấu giá
    @GetMapping("/{auctionId}/bids")
    public ResponseEntity<List<Bid>> getBidsForAuction(@PathVariable Long auctionId) {
        try {
            List<Bid> bids = auctionService.getBidsForAuction(auctionId);
            return ResponseEntity.ok(bids);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
