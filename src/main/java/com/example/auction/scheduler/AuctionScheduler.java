package com.example.auction.scheduler;

import com.example.auction.model.Auction;
import com.example.auction.repository.AuctionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {

    @Autowired
    private AuctionRepository auctionRepository;

    @Scheduled(fixedRate = 60000) // Chạy mỗi phút
    public void checkAuctionExpiry() {
        List<Auction> auctions = auctionRepository.findByIsExpired(false);
        for (Auction auction : auctions) {
            if (auction.getEndTime().isBefore(LocalDateTime.now())) {
                auction.setExpired(true);
                auctionRepository.save(auction); // Cập nhật trạng thái
                System.out.println("Auction expired: " + auction.getName());
            }
        }
    }
}
