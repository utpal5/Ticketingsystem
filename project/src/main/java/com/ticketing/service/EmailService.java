package com.ticketing.service;

import com.ticketing.model.Comment;
import com.ticketing.model.Ticket;
import com.ticketing.model.TicketStatus;
import com.ticketing.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendTicketCreatedNotification(Ticket ticket) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(ticket.getCreator().getEmail());
            message.setSubject("Ticket Created - #" + ticket.getId());
            message.setText(String.format(
                    "Dear %s,\n\n" +
                    "Your ticket has been created successfully.\n\n" +
                    "Ticket ID: #%d\n" +
                    "Subject: %s\n" +
                    "Priority: %s\n" +
                    "Status: %s\n\n" +
                    "We will get back to you soon.\n\n" +
                    "Best regards,\n" +
                    "Support Team",
                    ticket.getCreator().getFullName(),
                    ticket.getId(),
                    ticket.getSubject(),
                    ticket.getPriority(),
                    ticket.getStatus()
            ));
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send ticket creation email: " + e.getMessage());
        }
    }

    @Async
    public void sendTicketStatusChangedNotification(Ticket ticket, TicketStatus oldStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(ticket.getCreator().getEmail());
            message.setSubject("Ticket Status Updated - #" + ticket.getId());
            message.setText(String.format(
                    "Dear %s,\n\n" +
                    "The status of your ticket has been updated.\n\n" +
                    "Ticket ID: #%d\n" +
                    "Subject: %s\n" +
                    "Previous Status: %s\n" +
                    "Current Status: %s\n\n" +
                    "Best regards,\n" +
                    "Support Team",
                    ticket.getCreator().getFullName(),
                    ticket.getId(),
                    ticket.getSubject(),
                    oldStatus,
                    ticket.getStatus()
            ));
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send status change email: " + e.getMessage());
        }
    }

    @Async
    public void sendTicketAssignedNotification(Ticket ticket, User oldAssignee) {
        try {
            // Notify new assignee
            if (ticket.getAssignee() != null) {
                SimpleMailMessage assigneeMessage = new SimpleMailMessage();
                assigneeMessage.setFrom(fromEmail);
                assigneeMessage.setTo(ticket.getAssignee().getEmail());
                assigneeMessage.setSubject("Ticket Assigned - #" + ticket.getId());
                assigneeMessage.setText(String.format(
                        "Dear %s,\n\n" +
                        "A ticket has been assigned to you.\n\n" +
                        "Ticket ID: #%d\n" +
                        "Subject: %s\n" +
                        "Priority: %s\n" +
                        "Created by: %s\n\n" +
                        "Please review and take appropriate action.\n\n" +
                        "Best regards,\n" +
                        "Support Team",
                        ticket.getAssignee().getFullName(),
                        ticket.getId(),
                        ticket.getSubject(),
                        ticket.getPriority(),
                        ticket.getCreator().getFullName()
                ));
                mailSender.send(assigneeMessage);
            }

            // Notify ticket creator
            SimpleMailMessage creatorMessage = new SimpleMailMessage();
            creatorMessage.setFrom(fromEmail);
            creatorMessage.setTo(ticket.getCreator().getEmail());
            creatorMessage.setSubject("Ticket Assignment Update - #" + ticket.getId());
            creatorMessage.setText(String.format(
                    "Dear %s,\n\n" +
                    "Your ticket has been assigned to a support agent.\n\n" +
                    "Ticket ID: #%d\n" +
                    "Subject: %s\n" +
                    "Assigned to: %s\n\n" +
                    "Best regards,\n" +
                    "Support Team",
                    ticket.getCreator().getFullName(),
                    ticket.getId(),
                    ticket.getSubject(),
                    ticket.getAssignee() != null ? ticket.getAssignee().getFullName() : "Unassigned"
            ));
            mailSender.send(creatorMessage);
        } catch (Exception e) {
            System.err.println("Failed to send assignment email: " + e.getMessage());
        }
    }

    @Async
    public void sendCommentAddedNotification(Ticket ticket, Comment comment) {
        try {
            // Don't send notifications for internal comments to regular users
            if (comment.isInternal()) {
                return;
            }

            String recipientEmail = null;
            String recipientName = null;

            // If comment is from creator, notify assignee
            if (comment.getAuthor().equals(ticket.getCreator()) && ticket.getAssignee() != null) {
                recipientEmail = ticket.getAssignee().getEmail();
                recipientName = ticket.getAssignee().getFullName();
            }
            // If comment is from assignee, notify creator
            else if (ticket.getAssignee() != null && comment.getAuthor().equals(ticket.getAssignee())) {
                recipientEmail = ticket.getCreator().getEmail();
                recipientName = ticket.getCreator().getFullName();
            }

            if (recipientEmail != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(recipientEmail);
                message.setSubject("New Comment on Ticket - #" + ticket.getId());
                message.setText(String.format(
                        "Dear %s,\n\n" +
                        "A new comment has been added to ticket #%d.\n\n" +
                        "Subject: %s\n" +
                        "Comment by: %s\n" +
                        "Comment: %s\n\n" +
                        "Best regards,\n" +
                        "Support Team",
                        recipientName,
                        ticket.getId(),
                        ticket.getSubject(),
                        comment.getAuthor().getFullName(),
                        comment.getContent()
                ));
                mailSender.send(message);
            }
        } catch (Exception e) {
            System.err.println("Failed to send comment notification email: " + e.getMessage());
        }
    }
}