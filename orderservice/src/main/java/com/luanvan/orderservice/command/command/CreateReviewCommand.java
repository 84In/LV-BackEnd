package com.luanvan.orderservice.command.command;

import com.luanvan.orderservice.entity.PaymentStatus;
import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewCommand {
    @TargetAggregateIdentifier
    private String id;
    private Integer rating;
    private String comment;
    private String userId;
    private String orderDetailId;
    private String productId;
}
