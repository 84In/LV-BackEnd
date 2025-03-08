package com.luanvan.userservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdatedEvent {
    private String id;
    private String email;
    private String phone;
    private String lastName;
    private String firstName;
    private String roleName;
}
