package com.luanvan.commonservice.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RollBackStockProductCommand {
    @TargetAggregateIdentifier
    private String id;
    private Long quantity;
    private String colorId;
    private String sizeId;
}
