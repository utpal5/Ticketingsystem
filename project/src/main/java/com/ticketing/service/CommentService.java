package com.ticketing.service;

import com.ticketing.dto.CommentRequest;
import com.ticketing.model.Comment;
import com.ticketing.model.Role;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import com.ticketing.repository.CommentRepository;
import com.ticketing.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private EmailService emailService;

    public List<Comment> getTicketComments(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check if user can view this ticket
        if (!canUserViewTicket(ticket, currentUser)) {
            throw new AccessDeniedException("You don't have permission to view this ticket");
        }

        // If user is not support agent or admin, only show non-internal comments
        if (currentUser.getRole() == Role.USER) {
            return commentRepository.findByTicketAndIsInternalFalseOrderByCreatedAtAsc(ticket);
        } else {
            return commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
        }
    }

    public Comment addComment(Long ticketId, CommentRequest request, User author) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Check if user can comment on this ticket
        if (!canUserCommentOnTicket(ticket, author)) {
            throw new AccessDeniedException("You don't have permission to comment on this ticket");
        }

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTicket(ticket);
        comment.setAuthor(author);
        
        // Only support agents and admins can create internal comments
        if (request.isInternal() && (author.getRole() == Role.SUPPORT_AGENT || author.getRole() == Role.ADMIN)) {
            comment.setInternal(true);
        }

        Comment savedComment = commentRepository.save(comment);
        
        // Send email notification
        emailService.sendCommentAddedNotification(ticket, savedComment);
        
        return savedComment;
    }

    private boolean canUserViewTicket(Ticket ticket, User user) {
        return user.getRole() == Role.ADMIN ||
               ticket.getCreator().equals(user) ||
               (user.getRole() == Role.SUPPORT_AGENT && ticket.getAssignee() != null && ticket.getAssignee().equals(user));
    }

    private boolean canUserCommentOnTicket(Ticket ticket, User user) {
        return user.getRole() == Role.ADMIN ||
               ticket.getCreator().equals(user) ||
               (user.getRole() == Role.SUPPORT_AGENT && ticket.getAssignee() != null && ticket.getAssignee().equals(user));
    }
}