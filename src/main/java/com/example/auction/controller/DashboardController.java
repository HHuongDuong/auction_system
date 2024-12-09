package com.example.auction.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @GetMapping
    public ResponseEntity<String> showDashboardPage() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/dashboard.html");
        String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(html);
    }
}