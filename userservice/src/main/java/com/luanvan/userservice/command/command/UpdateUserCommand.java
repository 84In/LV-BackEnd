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
public class UpdateUserCommand {
    @TargetAggregateIdentifier
    private String id;
    private String email;
    private String phone;
    private String lastName;
    private String firstName;
    private String roleName;

}
