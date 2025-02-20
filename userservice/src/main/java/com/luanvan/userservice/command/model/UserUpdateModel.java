package com.luanvan.userservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateModel {
    private String email;
    private String phone;
    private String lastName;
    private String firstName;
    private String roleName;
}
