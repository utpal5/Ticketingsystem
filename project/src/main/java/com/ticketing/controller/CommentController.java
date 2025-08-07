package com.ticketing.controller;

import com.ticketing.dto.CommentRequest;
import com.ticketing.model.Comment;
import com.ticketing.model.User;
import com.ticketing.security.UserPrincipal;
import com.ticketing.service.CommentService;
import com.ticketing.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getTicketComments(
            @PathVariable Long ticketId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {
            User user = userRepository.findByUsername(userPrincipal.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Comment> comments = commentService.getTicketComments(ticketId, user);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addComment(
            @PathVariable Long ticketId,
            @Valid @RequestBody CommentRequest commentRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {
            User user = userRepository.findByUsername(userPrincipal.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Comment comment = commentService.addComment(ticketId, commentRequest, user);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}