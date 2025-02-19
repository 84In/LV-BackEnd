package com.luanvan.productservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryCommand {
    @TargetAggregateIdentifier
    private String id;
    private String name;
    private String codeName;
    private String description;
    private String images;
    private Boolean isActive;
}
