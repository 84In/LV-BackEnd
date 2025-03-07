package com.luanvan.userservice.dto;

import com.luanvan.userservice.entity.District;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WardResponseModel {

    private Integer id;
    private String codeName;
    private String name;
    private String divisionType;
}
