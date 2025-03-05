package com.luanvan.orderservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {
    @Id
    private String id;

    @Column(name = "carrier_name")
    private String carrierName;

    @Column(name = "tracking_code")
    private String trackingCode;

    @Column(name = "shipping_cost")
    private BigDecimal shippingCost;

    @Column(name = "recipent_name")
    private String recipientName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "province")
    private String province;

    @Column(name = "district")
    private String district;

    @Column(name = "ward")
    private String ward;

    @Column(name = "street")
    private String street;

    @Column(name = "address")
    private String address;

    @CreationTimestamp
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @UpdateTimestamp
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToOne(mappedBy = "delivery")
    private Order order;
}
