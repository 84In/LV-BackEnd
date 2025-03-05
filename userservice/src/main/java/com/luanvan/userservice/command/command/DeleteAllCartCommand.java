package com.luanvan.userservice.command.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteAllCartCommand {
    @TargetAggregateIdentifier
    private String id;
}
