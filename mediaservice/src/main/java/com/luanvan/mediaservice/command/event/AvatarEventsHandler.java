package com.luanvan.mediaservice.command.event;


import com.luanvan.commonservice.event.AvatarUploadedEvent;
import com.luanvan.commonservice.services.KafkaService;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AvatarEventsHandler {

    @Autowired
    private KafkaService kafkaService;

    @EventHandler
    public void on(AvatarUploadedEvent event) {
        log.info(event.toString());
        kafkaService.sendMessage("avatar-uploaded-topic", event);
    }

}
