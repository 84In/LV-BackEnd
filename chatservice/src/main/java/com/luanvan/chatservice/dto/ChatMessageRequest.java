package com.luanvan.chatservice.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {
    private String sessionId;
    private String message;
}
