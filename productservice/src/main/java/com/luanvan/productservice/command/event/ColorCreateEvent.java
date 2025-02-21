package com.luanvan.productservice.command.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorCreateEvent {
    private String id;
    private String name;
    private String codeName;
    private String colorCode;
    private String description;
    private Boolean isActive;
}
