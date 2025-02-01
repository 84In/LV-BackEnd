package com.luanvan.mediaservice.command.event;


import com.luanvan.commonservice.event.AvatarUploadedEvent;
import com.luanvan.commonservice.services.KafkaService;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AvatarEventsHandler {
    @Autowired
    private KafkaService kafkaService;

    @EventHandler
    public void on(AvatarUploadedEvent event) {
        kafkaService.sendMessage("avatar-uploaded-topic", event);
    }

}
