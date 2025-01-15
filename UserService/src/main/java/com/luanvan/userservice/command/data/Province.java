package com.luanvan.userservice.command.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "provinces")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Province {
    @Id
    private Integer code;
    private String name;
    private String codeName;
    private String divisionType;
    private Boolean isActive = true;
}
