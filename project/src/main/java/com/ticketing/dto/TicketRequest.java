package com.ticketing.dto;

import com.ticketing.model.Priority;
import jakarta.validation.constraints.NotBlank;

public class TicketRequest {
    @NotBlank(message = "Subject is required")
    private String subject;

    private String description;
    
    private Priority priority = Priority.MEDIUM;

    // Constructors
    public TicketRequest() {}

    public TicketRequest(String subject, String description, Priority priority) {
        this.subject = subject;
        this.description = description;
        this.priority = priority;
    }

    // Getters and Setters
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}