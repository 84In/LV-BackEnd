package com.luanvan.userservice.command.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    @Id
    private String id;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "house_number_and_street", nullable = false)
    private String houseNumberAndStreet;

    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @ManyToOne
    @JoinColumn(name = "ward_id") // Dùng khóa ngoại là `ward_code` của Ward
    private Ward ward;


    @CreationTimestamp
    @Column(name = "created_at", updatable = false) // Không cho phép cập nhật giá trị này
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at") // Tự động cập nhật khi có thay đổi
    private LocalDateTime updatedAt;

}
