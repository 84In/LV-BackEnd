package com.luanvan.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateModel {
    private String username;
    private String password;
    private Boolean active;
    private String email;
    private String phone;
    private String lastName;
    private String firstName;
    private String avatar;
    private String roleName;
}
