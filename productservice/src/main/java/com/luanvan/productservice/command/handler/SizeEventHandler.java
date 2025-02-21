package com.luanvan.productservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.command.event.SizeCreateEvent;
import com.luanvan.productservice.command.event.SizeDeleteEvent;
import com.luanvan.productservice.command.event.SizeUpdateEvent;
import com.luanvan.productservice.entity.Size;
import com.luanvan.productservice.repository.SizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SizeEventHandler {
    private final SizeRepository sizeRepository;

    @EventHandler
    public void on(SizeCreateEvent event) throws Exception {
        log.info("Size created");
        var size = Size.builder()
                .id(event.getId())
                .name(event.getName())
                .codeName(event.getCodeName())
                .isActive(event.getIsActive())
                .build();
        sizeRepository.save(size);
    }

    @EventHandler
    public void on(SizeUpdateEvent event) {
        log.info("Size updated");
        var size = sizeRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));
        size.setName(event.getName());
        size.setCodeName(event.getCodeName());
        size.setIsActive(event.getIsActive());
        sizeRepository.save(size);
    }

    @EventHandler
    @DisallowReplay
    public void on(SizeDeleteEvent event) {
        log.info("Size deleted");
        var size = sizeRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));
        sizeRepository.delete(size);
    }
}
