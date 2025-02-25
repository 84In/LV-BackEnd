package com.luanvan.commonservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImagesUploadModel {
    private String productId;
    private ArrayList<String> imageUrls;

}
