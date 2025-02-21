package com.luanvan.productservice.command.service;

import com.luanvan.commonservice.advice.AppException;
import com.luanvan.commonservice.advice.ErrorCode;
import com.luanvan.productservice.command.command.CreatePromotionCommand;
import com.luanvan.productservice.command.command.DeletePromotionCommand;
import com.luanvan.productservice.command.command.DeleteSizeCommand;
import com.luanvan.productservice.command.command.UpdatePromotionCommand;
import com.luanvan.productservice.command.model.PromotionCreateModel;
import com.luanvan.productservice.command.model.PromotionUpdateModel;
import com.luanvan.productservice.repository.PromotionRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class PromotionCommandService {
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private CommandGateway commandGateway;

    public HashMap<?, ?> save(PromotionCreateModel model) throws AppException {
        if (promotionRepository.existsByName(model.getName())) {
            throw new AppException(ErrorCode.PROMOTION_EXISTED);
        } else {
            CreatePromotionCommand command = CreatePromotionCommand
                    .builder()
                    .id(UUID.randomUUID().toString())
                    .name(model.getName())
                    .codeName(model.getCodeName())
                    .description(model.getDescription())
                    .discountPercentage(model.getDiscountPercentage())
                    .startDate(model.getStartDate())
                    .endDate(model.getEndDate())
                    .isActive(true)
                    .build();
            var result = new HashMap<>();
            result.put("id", commandGateway.sendAndWait(command));
            return result;
        }
    }

    public HashMap<?, ?> update(String id, PromotionUpdateModel model) throws AppException {
        if (!promotionRepository.existsById(id)) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXISTED);
        }
        UpdatePromotionCommand command = UpdatePromotionCommand.builder()
                .id(id)
                .name(model.getName())
                .codeName(model.getCodeName())
                .description(model.getDescription())
                .discountPercentage(model.getDiscountPercentage())
                .startDate(model.getStartDate())
                .endDate(model.getEndDate())
                .isActive(model.getIsActive())
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

    public HashMap<?, ?> delete(String id) throws AppException {
        if (!promotionRepository.existsById(id)) {
            throw new AppException(ErrorCode.PROMOTION_NOT_EXISTED);
        }
        DeletePromotionCommand command = DeletePromotionCommand.builder()
                .id(id)
                .build();
        var result = new HashMap<>();
        result.put("id", commandGateway.sendAndWait(command));
        return result;
    }

}
