package com.luanvan.orderservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

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
