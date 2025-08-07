package com.ticketing.controller;

import com.ticketing.dto.RatingRequest;
import com.ticketing.model.Rating;
import com.ticketing.model.User;
import com.ticketing.security.UserPrincipal;
import com.ticketing.service.RatingService;
import com.ticketing.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tickets/{ticketId}/rating")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getTicketRating(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {
            User user = userRepository.findByUsername(userPrincipal.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Optional<Rating> rating = ratingService.getTicketRating(ticketId, user);
            if (rating.isPresent()) {
                return ResponseEntity.ok(rating.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> rateTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody RatingRequest ratingRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {
            User user = userRepository.findByUsername(userPrincipal.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Rating rating = ratingService.rateTicket(ticketId, ratingRequest, user);
            return ResponseEntity.ok(rating);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}