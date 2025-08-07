package com.ticketing.controller;

import com.ticketing.dto.UserRegistrationRequest;
import com.ticketing.dto.UserResponse;
import com.ticketing.model.*;
import com.ticketing.security.UserPrincipal;
import com.ticketing.service.TicketService;
import com.ticketing.service.UserService;
import com.ticketing.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private UserRepository userRepository;

    // User Management
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<UserResponse> users = userService.getUsersWithFilters(search, role, active, pageable);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            UserResponse user = userService.createUser(request);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestParam Role role) {
        try {
            UserResponse user = userService.updateUserRole(id, role);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/users/{id}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        try {
            UserResponse user = userService.toggleUserStatus(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Ticket Management
    @GetMapping("/tickets")
    public ResponseEntity<?> getAllTickets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Ticket> tickets = ticketService.getTicketsWithFilters(
                    search, status, priority, creatorId, assigneeId, pageable);
            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/tickets/{id}/force-assign")
    public ResponseEntity<?> forceAssignTicket(
            @PathVariable Long id,
            @RequestParam Long assigneeId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {
            User admin = userRepository.findByUsername(userPrincipal.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Ticket ticket = ticketService.assignTicket(id, assigneeId, admin);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/tickets/{id}/force-status")
    public ResponseEntity<?> forceUpdateTicketStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {
            User admin = userRepository.findByUsername(userPrincipal.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Ticket ticket = ticketService.updateTicketStatus(id, status, admin);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<?> forceDeleteTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        try {
            User admin = userRepository.findByUsername(userPrincipal.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ticketService.deleteTicket(id, admin);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Dashboard Statistics
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Ticket statistics
            stats.put("totalTickets", ticketService.getTicketCountByStatus(null));
            stats.put("openTickets", ticketService.getTicketCountByStatus(TicketStatus.OPEN));
            stats.put("inProgressTickets", ticketService.getTicketCountByStatus(TicketStatus.IN_PROGRESS));
            stats.put("resolvedTickets", ticketService.getTicketCountByStatus(TicketStatus.RESOLVED));
            stats.put("closedTickets", ticketService.getTicketCountByStatus(TicketStatus.CLOSED));
            
            // Priority statistics
            stats.put("urgentTickets", ticketService.getTicketCountByPriority(Priority.URGENT));
            stats.put("highPriorityTickets", ticketService.getTicketCountByPriority(Priority.HIGH));
            stats.put("mediumPriorityTickets", ticketService.getTicketCountByPriority(Priority.MEDIUM));
            stats.put("lowPriorityTickets", ticketService.getTicketCountByPriority(Priority.LOW));
            
            // User statistics
            stats.put("totalUsers", userService.getAllUsers().size());
            stats.put("supportAgents", userService.getSupportAgents().size());
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/support-agents")
    public ResponseEntity<?> getSupportAgents() {
        try {
            List<UserResponse> agents = userService.getSupportAgents();
            return ResponseEntity.ok(agents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}