package com.ticketing.service;

import com.ticketing.dto.TicketRequest;
import com.ticketing.model.*;
import com.ticketing.repository.TicketRepository;
import com.ticketing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Page<Ticket> getTicketsWithFilters(String search, TicketStatus status, Priority priority, 
                                            Long creatorId, Long assigneeId, Pageable pageable) {
        return ticketRepository.findTicketsWithFilters(search, status, priority, creatorId, assigneeId, pageable);
    }

    public Page<Ticket> getUserTicketsWithFilters(User user, String search, TicketStatus status, 
                                                Priority priority, Pageable pageable) {
        return ticketRepository.findUserTicketsWithFilters(user, search, status, priority, pageable);
    }

    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public List<Ticket> getUserTickets(User user) {
        return ticketRepository.findByCreatorOrderByCreatedAtDesc(user);
    }

    public List<Ticket> getAssignedTickets(User agent) {
        return ticketRepository.findByAssigneeOrderByCreatedAtDesc(agent);
    }

    public Ticket createTicket(TicketRequest request, User creator) {
        Ticket ticket = new Ticket();
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());
        ticket.setCreator(creator);

        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Send email notification
        emailService.sendTicketCreatedNotification(savedTicket);
        
        return savedTicket;
    }

    public Ticket updateTicket(Long ticketId, TicketRequest request, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check if user can update this ticket
        if (!canUserModifyTicket(ticket, currentUser)) {
            throw new AccessDeniedException("You don't have permission to update this ticket");
        }

        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());

        return ticketRepository.save(ticket);
    }

    public Ticket updateTicketStatus(Long ticketId, TicketStatus status, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check permissions
        if (!canUserChangeTicketStatus(ticket, currentUser)) {
            throw new AccessDeniedException("You don't have permission to change ticket status");
        }

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(status);
        
        if (status == TicketStatus.RESOLVED && oldStatus != TicketStatus.RESOLVED) {
            ticket.setResolvedAt(LocalDateTime.now());
        } else if (status == TicketStatus.CLOSED && oldStatus != TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Send email notification
        emailService.sendTicketStatusChangedNotification(savedTicket, oldStatus);
        
        return savedTicket;
    }

    public Ticket assignTicket(Long ticketId, Long assigneeId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));

        // Check if assignee is a support agent or admin
        if (assignee.getRole() != Role.SUPPORT_AGENT && assignee.getRole() != Role.ADMIN) {
            throw new RuntimeException("Can only assign tickets to support agents or admins");
        }

        // Check permissions
        if (!canUserAssignTicket(ticket, currentUser)) {
            throw new AccessDeniedException("You don't have permission to assign this ticket");
        }

        User oldAssignee = ticket.getAssignee();
        ticket.setAssignee(assignee);
        
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Send email notifications
        emailService.sendTicketAssignedNotification(savedTicket, oldAssignee);
        
        return savedTicket;
    }

    public void deleteTicket(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Only admins or ticket creators can delete tickets
        if (currentUser.getRole() != Role.ADMIN && !ticket.getCreator().equals(currentUser)) {
            throw new AccessDeniedException("You don't have permission to delete this ticket");
        }

        ticketRepository.delete(ticket);
    }

    private boolean canUserModifyTicket(Ticket ticket, User user) {
        return user.getRole() == Role.ADMIN || 
               ticket.getCreator().equals(user) ||
               (user.getRole() == Role.SUPPORT_AGENT && ticket.getAssignee() != null && ticket.getAssignee().equals(user));
    }

    private boolean canUserChangeTicketStatus(Ticket ticket, User user) {
        return user.getRole() == Role.ADMIN ||
               (user.getRole() == Role.SUPPORT_AGENT && ticket.getAssignee() != null && ticket.getAssignee().equals(user));
    }

    private boolean canUserAssignTicket(Ticket ticket, User user) {
        return user.getRole() == Role.ADMIN ||
               (user.getRole() == Role.SUPPORT_AGENT);
    }

    // Statistics methods
    public long getTicketCountByStatus(TicketStatus status) {
        return ticketRepository.countByStatus(status);
    }

    public long getTicketCountByPriority(Priority priority) {
        return ticketRepository.countByPriority(priority);
    }
}