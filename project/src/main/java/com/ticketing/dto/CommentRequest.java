package com.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {
    @NotBlank(message = "Content is required")
    private String content;
    
    private boolean isInternal = false;

    // Constructors
    public CommentRequest() {}

    public CommentRequest(String content, boolean isInternal) {
        this.content = content;
        this.isInternal = isInternal;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isInternal() {
        return isInternal;
    }

    public void setInternal(boolean internal) {
        isInternal = internal;
    }
}