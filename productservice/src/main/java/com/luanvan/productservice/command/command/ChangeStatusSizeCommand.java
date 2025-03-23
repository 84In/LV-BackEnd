package com.luanvan.productservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeStatusSizeCommand {
    @TargetAggregateIdentifier
    private String id;
    private Boolean isActive;
}
