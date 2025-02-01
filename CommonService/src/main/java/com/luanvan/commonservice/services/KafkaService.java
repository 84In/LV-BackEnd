package com.luanvan.commonservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaService {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    /**
     * Sends a message to the specified Kafka topic
     *
     * @Param topic the name of the Kafka topic to which the message will be sent
     * @Param message the message content to be sent
     */
    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
        log.info("send message:{}", message);
    }
}
