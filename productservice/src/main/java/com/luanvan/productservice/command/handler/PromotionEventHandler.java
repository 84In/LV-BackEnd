package com.luanvan.productservice.command.handler;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.commonservice.event.PromotionChangeStatusEvent;
import com.luanvan.productservice.command.event.PromotionCreateEvent;
import com.luanvan.productservice.command.event.PromotionDeleteEvent;
import com.luanvan.commonservice.event.PromotionUpdateEvent;
import com.luanvan.productservice.entity.Promotion;
import com.luanvan.productservice.repository.PromotionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PromotionEventHandler {
    private final PromotionRepository promotionRepository;

    @EventHandler
    public void on(PromotionCreateEvent event) throws Exception {
        log.info("Promotion created");
        var promotion = Promotion.builder()
                .id(event.getId())
                .name(event.getName())
                .codeName(event.getCodeName())
                .description(event.getDescription())
                .discountPercentage(event.getDiscountPercentage())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .isActive(event.getIsActive())
                .build();
        promotionRepository.save(promotion);
    }

    @EventHandler
    public void on(PromotionUpdateEvent event) {
        log.info("Promotion updated");
        var promotion = promotionRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED));
        promotion.setName(event.getName());
        promotion.setCodeName(event.getCodeName());
        promotion.setDescription(event.getDescription());
        promotion.setDiscountPercentage(event.getDiscountPercentage());
        promotion.setStartDate(event.getStartDate());
        promotion.setEndDate(event.getEndDate());
        promotion.setIsActive(event.getIsActive());
        promotionRepository.save(promotion);
    }

    @EventHandler
    public void on(PromotionChangeStatusEvent event){
        log.info("Promotion changed status");
        var promotion = promotionRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED));
        promotion.setIsActive(event.getIsActive());
        promotionRepository.save(promotion);
    }

    @EventHandler
    @DisallowReplay
    public void on(PromotionDeleteEvent event) {
        log.info("Promotion deleted");
        var promotion = promotionRepository.findById(event.getId()).orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_EXISTED));
        promotionRepository.delete(promotion);
    }
}
