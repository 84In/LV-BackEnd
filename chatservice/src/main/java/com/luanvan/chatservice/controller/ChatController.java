package com.luanvan.chatservice.controller;

import com.luanvan.chatservice.dto.ChatMessageRequest;
import com.luanvan.chatservice.entity.ChatMessage;
import com.luanvan.chatservice.repository.ChatMessageRepository;
import com.luanvan.chatservice.service.DialogflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {
    private final DialogflowService dialogflowService;
    private final ChatMessageRepository chatMessageRepository;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatMessageRequest request) throws Exception {
        String sessionId = request.getSessionId();
        String userMessage = request.getMessage();

        // Lưu tin nhắn của người dùng vào MongoDB
        chatMessageRepository.save(new ChatMessage(null, sessionId, "user", userMessage, Instant.now()));

        // Gửi tin nhắn đến Dialogflow
        String botResponse = dialogflowService.detectIntent(sessionId, userMessage);

        // Lưu phản hồi của bot vào MongoDB
        chatMessageRepository.save(new ChatMessage(null, sessionId, "bot", botResponse, Instant.now()));

        return ResponseEntity.ok(Map.of("response", botResponse));
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId));
    }

}
