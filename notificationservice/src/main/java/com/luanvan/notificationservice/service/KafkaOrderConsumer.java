package com.luanvan.notificationservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.luanvan.commonservice.model.response.OrderResponseModel;
import com.luanvan.commonservice.queries.GetOrderQuery;
import com.luanvan.notificationservice.handler.OrderWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaOrderConsumer {
    private final OrderWebSocketHandler webSocketHandler;
    private final QueryGateway queryGateway;

    @KafkaListener(topics = "order-send-admin", groupId = "order-group")
    public void consumeOrder(String orderId) throws IOException {
        log.info("Received order id {}", orderId);
        GetOrderQuery getOrderQuery = new GetOrderQuery(orderId);
        OrderResponseModel response = queryGateway.query(getOrderQuery, ResponseTypes.instanceOf(OrderResponseModel.class)).join();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println("Nhận order từ Kafka: " +orderId);
        webSocketHandler.sendOrderToClients(objectMapper.writeValueAsString(response));
    }
}
