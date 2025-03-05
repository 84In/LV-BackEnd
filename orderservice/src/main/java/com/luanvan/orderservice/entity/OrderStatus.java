package com.luanvan.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Entity
@Table(name = "order_status" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatus {
    @Id
    @Column(name = "code_name")
    private String codeName;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

}
