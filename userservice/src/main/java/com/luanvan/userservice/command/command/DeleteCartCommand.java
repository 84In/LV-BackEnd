package com.luanvan.userservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCartCommand {
    @TargetAggregateIdentifier
    private String id;
    private String cartDetailId;
}
