package com.luanvan.productservice.command.event;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.entity.Category;
import com.luanvan.productservice.entity.Size;
import com.luanvan.productservice.repository.CategoryRepository;
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
        var category = sizeRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));
        category.setName(event.getName());
        category.setCodeName(event.getCodeName());
        category.setIsActive(event.getIsActive());
        sizeRepository.save(category);
    }

    @EventHandler
    @DisallowReplay
    public void on(SizeDeleteEvent event) {
        log.info("Size deleted");
        var category = sizeRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.SIZE_NOT_EXISTED));
        sizeRepository.delete(category);
    }
}
