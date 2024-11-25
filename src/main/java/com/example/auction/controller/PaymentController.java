package com.example.auction.controller;

import com.example.auction.model.Payment;
import com.example.auction.model.PaymentStatus;
import com.example.auction.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitPayment(
            @RequestParam Long auctionId,
            @RequestParam String accountname,
            @RequestParam BigDecimal amount) {
        try {
            Payment payment = paymentService.submitPayment(auctionId, accountname, amount);
            return ResponseEntity.ok("Payment submitted successfully. Payment ID: " + payment.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/proof")
    public ResponseEntity<?> submitProofOfPayment(
            @PathVariable Long id,
            @RequestParam("proofOfPayment") MultipartFile proofOfPayment) {
        try {
            if (proofOfPayment.isEmpty()) {
                throw new RuntimeException("Proof of payment file is required.");
            }

            // Save the file to a directory (e.g., "uploads/")
            String filePath = saveFile(proofOfPayment);

            // Save the file path in the payment record
            Payment payment = paymentService.submitPaymentProof(id, filePath);
            return ResponseEntity.ok("Proof of payment submitted successfully for payment ID: " + payment.getId());
        } catch (IOException e) {
            // Handle file system exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save the file. Please try again.");
        } catch (RuntimeException e) {
            // Handle other application exceptions
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String saveFile(MultipartFile file) throws IOException {
        String uploadDir = "uploads/";
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<?> confirmPayment(@PathVariable Long id) {
        try {
            Payment payment = paymentService.confirmPayment(id);
            return ResponseEntity.ok("Payment confirmed successfully for payment ID: " + payment.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id, @RequestParam PaymentStatus status) {
        Payment payment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok("Payment status updated to: " + payment.getPaymentStatus());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Payment>> getPaymentsByUsername(@PathVariable String username) {
        List<Payment> payments = paymentService.getPaymentsByUsername(username);
        return ResponseEntity.ok(payments);
    }
}
