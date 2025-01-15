package com.luanvan.userservice.command.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "districts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class District {

    @Id
    private Integer code;
    private String name;
    private String codeName;
    private String divisionCode;

    private Boolean isActive = true;

    @ManyToOne
    private Province province;


}
