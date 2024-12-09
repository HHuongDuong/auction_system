package com.example.auction.service;

import com.example.auction.model.Auction;
import com.example.auction.model.Payment;
import com.example.auction.model.PaymentStatus;
import com.example.auction.repository.PaymentRepository;
import com.example.auction.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Payment submitPayment(Long auctionId, String accountname, BigDecimal amount) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found!"));

        if (LocalDateTime.now().isBefore(auction.getEndTime())) {
            throw new RuntimeException("Cannot submit payment before the auction ends.");
        }

        Payment payment = new Payment();
        payment.setAccountname(accountname);
        payment.setAmount(amount);
        payment.setAuction(auction); // Associate payment with auction
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment submitPaymentProof(Long paymentId, String proofOfPayment) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));

        Auction auction = payment.getAuction();

        // Check if within 48 hours of auction end time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = auction.getEndTime().plusHours(48);
        if (now.isAfter(deadline)) {
            throw new RuntimeException("The 48-hour window for submitting payment proof has passed.");
        }

        payment.setProofOfPayment(proofOfPayment);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }


    @Transactional
    public Payment confirmPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));

        Auction auction = payment.getAuction();

        // Check if within 48 hours of auction end time
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = auction.getEndTime().plusHours(48);
        if (now.isAfter(deadline)) {
            throw new RuntimeException("The 48-hour window for confirming payment has passed.");
        }

        payment.setSellerConfirmation(true);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment updatePaymentStatus(Long paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found!"));

        payment.setPaymentStatus(status);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByUsername(String accountname) {
        return paymentRepository.findByAccountname(accountname);
    }
}
