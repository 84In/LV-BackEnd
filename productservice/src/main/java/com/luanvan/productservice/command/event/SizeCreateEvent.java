package com.luanvan.productservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SizeCreateEvent {
    private String id;
    private String name;
    private String codeName;
    private Boolean isActive;
}
