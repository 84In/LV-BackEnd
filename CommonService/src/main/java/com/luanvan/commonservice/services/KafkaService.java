package com.luanvan.commonservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Sends a message to the specified Kafka topic.
     *
     * @param topic the name of the Kafka topic to which the message will be sent
     * @param message the message content to be sent
     */

    public void sendMessage(String topic, Object message){

        try {
            kafkaTemplate.send(topic, objectMapper.writeValueAsString(message));
            log.info("Message sent to topic: {}",topic);
        }catch (JsonProcessingException e) {
            log.error("Kafka send message error: {}", e.getMessage());
        }
    }
}
