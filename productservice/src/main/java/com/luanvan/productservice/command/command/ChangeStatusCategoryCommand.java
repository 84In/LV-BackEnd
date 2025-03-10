package com.luanvan.productservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangeStatusCategoryCommand {
    @TargetAggregateIdentifier
    private String id;
    private Boolean isActive;
}
