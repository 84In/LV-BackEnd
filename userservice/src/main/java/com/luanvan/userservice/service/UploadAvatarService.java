package com.luanvan.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.commonservice.model.AvatarUpdateModel;
import com.luanvan.userservice.entity.User;
import com.luanvan.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadAvatarService {

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "avatar-uploaded-topic", groupId = "user-group")
    public void on(String message){

        System.out.println(message);

        try {
            AvatarUpdateModel model = objectMapper.readValue(message, AvatarUpdateModel.class);
            User user = userRepository.findById(model.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
            user.setAvatar(model.getAvatarUrl());
            userRepository.save(user);
            log.info("Avatar URL updated for User ID: {}", model.getUserId());
        }catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }
}
