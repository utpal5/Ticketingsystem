package com.ticketing.service;

import com.ticketing.dto.RatingRequest;
import com.ticketing.model.Rating;
import com.ticketing.model.Ticket;
import com.ticketing.model.TicketStatus;
import com.ticketing.model.User;
import com.ticketing.repository.RatingRepository;
import com.ticketing.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public Rating rateTicket(Long ticketId, RatingRequest request, User rater) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Only ticket creator can rate the ticket
        if (!ticket.getCreator().equals(rater)) {
            throw new AccessDeniedException("Only the ticket creator can rate the resolution");
        }

        // Ticket must be resolved or closed to be rated
        if (ticket.getStatus() != TicketStatus.RESOLVED && ticket.getStatus() != TicketStatus.CLOSED) {
            throw new RuntimeException("Ticket must be resolved or closed before rating");
        }

        // Check if ticket is already rated
        Optional<Rating> existingRating = ratingRepository.findByTicket(ticket);
        if (existingRating.isPresent()) {
            throw new RuntimeException("Ticket has already been rated");
        }

        Rating rating = new Rating(
                request.getRating(),
                request.getFeedback(),
                ticket,
                rater
        );

        return ratingRepository.save(rating);
    }

    public Optional<Rating> getTicketRating(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check if user can view this ticket rating
        if (!canUserViewTicketRating(ticket, currentUser)) {
            throw new AccessDeniedException("You don't have permission to view this ticket rating");
        }

        return ratingRepository.findByTicket(ticket);
    }

    public Double getAverageRating() {
        return ratingRepository.findAverageRating();
    }

    public Double getAverageRatingForAgent(User agent) {
        return ratingRepository.findAverageRatingByAssignee(agent);
    }

    private boolean canUserViewTicketRating(Ticket ticket, User user) {
        return ticket.getCreator().equals(user) ||
               (ticket.getAssignee() != null && ticket.getAssignee().equals(user)) ||
               user.getRole().name().equals("ADMIN");
    }
}