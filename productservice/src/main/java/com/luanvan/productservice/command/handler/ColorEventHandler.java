package com.luanvan.productservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.event.ColorChangeStatusEvent;
import com.luanvan.commonservice.event.ColorUpdateEvent;
import com.luanvan.productservice.command.event.*;
import com.luanvan.productservice.entity.Color;
import com.luanvan.productservice.repository.ColorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ColorEventHandler {
    private final ColorRepository colorRepository;

    @EventHandler
    public void on(ColorCreateEvent event) throws Exception {
        log.info("Color created");
        var color = Color.builder()
                .id(event.getId())
                .name(event.getName())
                .codeName(event.getCodeName())
                .colorCode(event.getColorCode())
                .description(event.getDescription())
                .isActive(event.getIsActive())
                .build();
        colorRepository.save(color);
    }

    @EventHandler
    public void on(ColorUpdateEvent event) {
        log.info("Color updated");
        var color = colorRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));
        color.setName(event.getName());
        color.setCodeName(event.getCodeName());
        color.setColorCode(event.getColorCode());
        color.setDescription(event.getDescription());
        color.setIsActive(event.getIsActive());
        colorRepository.save(color);
    }

    @EventHandler
    public void on(ColorChangeStatusEvent event) {
        log.info("Color changed status");
        var category = colorRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));
        category.setIsActive(event.getIsActive());
        colorRepository.save(category);
    }

    @EventHandler
    @DisallowReplay
    public void on(ColorDeleteEvent event) {
        log.info("Color deleted");
        var color = colorRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.COLOR_NOT_EXISTED));
        colorRepository.delete(color);
    }
}
