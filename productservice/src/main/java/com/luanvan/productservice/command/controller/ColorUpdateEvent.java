package com.luanvan.productservice.command.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorUpdateEvent {
    private String id;
    private String name;
    private String codeName;
    private String colorCode;
    private String description;
    private Boolean isActive;
}
