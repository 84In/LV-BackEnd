package com.luanvan.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "provinces")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Province {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code_name", nullable = false, length = 50)
    private String codeName;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "division_type", nullable = false, length = 50)
    private String divisionType;

    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    Set<District> districts = new HashSet<>();
}
