package com.ticketing.service;

import com.ticketing.model.Attachment;
import com.ticketing.model.Role;
import com.ticketing.model.Ticket;
import com.ticketing.model.User;
import com.ticketing.repository.AttachmentRepository;
import com.ticketing.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public List<Attachment> getTicketAttachments(Long ticketId, User currentUser) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!canUserViewTicket(ticket, currentUser)) {
            throw new AccessDeniedException("You don't have permission to view this ticket");
        }

        return attachmentRepository.findByTicket(ticket);
    }

    public Attachment uploadAttachment(Long ticketId, MultipartFile file, User uploader) throws IOException {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (!canUserUploadToTicket(ticket, uploader)) {
            throw new AccessDeniedException("You don't have permission to upload files to this ticket");
        }

        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        Attachment attachment = new Attachment(
                uniqueFilename,
                originalFilename,
                file.getContentType(),
                file.getSize(),
                filePath.toString(),
                ticket,
                uploader
        );

        return attachmentRepository.save(attachment);
    }

    public byte[] downloadAttachment(Long attachmentId, User currentUser) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        if (!canUserViewTicket(attachment.getTicket(), currentUser)) {
            throw new AccessDeniedException("You don't have permission to download this attachment");
        }

        Path filePath = Paths.get(attachment.getFilePath());
        return Files.readAllBytes(filePath);
    }

    public void deleteAttachment(Long attachmentId, User currentUser) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found"));

        if (!canUserDeleteAttachment(attachment, currentUser)) {
            throw new AccessDeniedException("You don't have permission to delete this attachment");
        }

        // Delete file from filesystem
        Path filePath = Paths.get(attachment.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete from database
        attachmentRepository.delete(attachment);
    }

    private boolean canUserViewTicket(Ticket ticket, User user) {
        return user.getRole() == Role.ADMIN ||
               ticket.getCreator().equals(user) ||
               (user.getRole() == Role.SUPPORT_AGENT && ticket.getAssignee() != null && ticket.getAssignee().equals(user));
    }

    private boolean canUserUploadToTicket(Ticket ticket, User user) {
        return user.getRole() == Role.ADMIN ||
               ticket.getCreator().equals(user) ||
               (user.getRole() == Role.SUPPORT_AGENT && ticket.getAssignee() != null && ticket.getAssignee().equals(user));
    }

    private boolean canUserDeleteAttachment(Attachment attachment, User user) {
        return user.getRole() == Role.ADMIN ||
               attachment.getUploadedBy().equals(user);
    }
}