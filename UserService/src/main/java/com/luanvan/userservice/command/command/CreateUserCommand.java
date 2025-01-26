package com.luanvan.userservice.command.command;

import com.luanvan.userservice.command.data.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserCommand {
    @TargetAggregateIdentifier
    private String id;
    private String username;
    private String password;
    private Boolean active;
    private String email;
    private String phone;
    private String lastName;
    private String firstName;
    private String roleName;

}
