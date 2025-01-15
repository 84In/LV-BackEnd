package com.luanvan.userservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProvinceCreatedEvent {
    private Integer code;
    private String name;
    private String codeName;
    private String divisionType;
    private Boolean isActive = true;
}
