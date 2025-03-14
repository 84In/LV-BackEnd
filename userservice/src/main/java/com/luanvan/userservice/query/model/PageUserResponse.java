package com.luanvan.userservice.query.model;

import com.luanvan.commonservice.model.response.PromotionResponseModel;
import com.luanvan.commonservice.model.response.UserResponseModel;
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
public class PageUserResponse implements Serializable {
    private ArrayList<UserResponseModel> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
