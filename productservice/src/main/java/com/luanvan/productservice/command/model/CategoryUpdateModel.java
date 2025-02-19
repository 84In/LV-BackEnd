package com.luanvan.productservice.command.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateModel {
    private String name;
    private String codeName;
    private String description;
    private String images;
    private Boolean isActive;
}
