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
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "code_name")
    private String codeName;

    @OneToMany(mappedBy = "orderStatus", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Collection<Order> order;

}
