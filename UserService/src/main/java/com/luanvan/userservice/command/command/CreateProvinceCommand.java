package com.luanvan.userservice.command.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProvinceCommand {
    @TargetAggregateIdentifier
    private Integer code;
    private String name;
    private String codeName;
    private String divisionType;
    private Boolean isActive = true;
}
