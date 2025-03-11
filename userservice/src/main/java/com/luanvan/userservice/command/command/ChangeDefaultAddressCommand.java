package com.luanvan.userservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeDefaultAddressCommand {
    @TargetAggregateIdentifier
    private String id;
    private String userId;
    private Boolean isDefault;
}
