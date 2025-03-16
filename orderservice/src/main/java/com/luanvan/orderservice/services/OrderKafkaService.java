package com.luanvan.orderservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrder(String orderId) {

        kafkaTemplate.send("order-send-admin", orderId);
        log.info("Sent order-send-admin to kafka topic orderId={}", orderId);

    }


}
