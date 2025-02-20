package com.luanvan.productservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteSizeCommand {
    @TargetAggregateIdentifier
    private String id;
}
