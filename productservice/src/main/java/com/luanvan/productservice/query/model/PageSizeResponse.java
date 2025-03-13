package com.luanvan.productservice.query.model;

import com.luanvan.commonservice.model.response.ProductResponseModel;
import com.luanvan.commonservice.model.response.SizeResponseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageSizeResponse implements Serializable {
    private ArrayList<SizeResponseModel> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}

