package com.luanvan.productservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColorUpdateModel {
    private String name;
    private String codeName;
    private String colorCode;
    private String description;
    private Boolean isActive;
}
