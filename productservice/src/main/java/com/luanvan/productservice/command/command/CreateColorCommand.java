package com.luanvan.productservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateColorCommand {
    @TargetAggregateIdentifier
    private String id;
    private String name;
    private String codeName;
    private String colorCode;
    private String description;
    private Boolean isActive;
}
