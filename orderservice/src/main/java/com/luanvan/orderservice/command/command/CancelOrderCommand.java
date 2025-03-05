package com.luanvan.orderservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CancelOrderCommand {
    @TargetAggregateIdentifier
    private String id;
}
