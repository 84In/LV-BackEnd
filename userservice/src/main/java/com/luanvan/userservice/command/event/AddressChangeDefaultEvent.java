package com.luanvan.userservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressChangeDefaultEvent {
    private String id;
    private String userId;
    private Boolean isDefault;
}
