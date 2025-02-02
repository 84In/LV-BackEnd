package com.luanvan.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.commonservice.event.AvatarUploadedEvent;
import com.luanvan.userservice.command.data.User;
import com.luanvan.userservice.command.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UploadAvatarService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "avatar-uploaded-topic", groupId = "user-group")
    public void on(String message) throws JsonProcessingException {

        System.out.println(message);

        AvatarUploadedEvent event = new ObjectMapper().readValue(message, AvatarUploadedEvent.class);
        User user = userRepository.findById(((AvatarUploadedEvent) event).getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar(((AvatarUploadedEvent) event).getAvatarUrl());
        userRepository.save(user);
        log.info("Avatar URL updated for User ID: {}", ((AvatarUploadedEvent) event).getUserId());


    }
}
