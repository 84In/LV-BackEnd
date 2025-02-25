package com.luanvan.commonservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryImageUpdateModel {
    private String categoryId;
    private String categoryUrl;
}
