package com.luanvan.orderservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeStatusOrderCommand {
    @TargetAggregateIdentifier
    private String id;
    private String orderStatus;
}
